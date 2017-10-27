package io.omengye.ws.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import io.omengye.ws.common.base.Constants;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;

import io.omengye.ws.service.FileService;
import io.omengye.ws.service.HttpClientService;
import io.omengye.ws.service.SearchService;
import io.omengye.ws.utils.FileUtil;


@RestController
public class IndexController {
	
	@Autowired
	private HttpClientService httpClientService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SearchService searchService;
	
	//上传路径
	private String path = "/var/www/html/";
	
	@GetMapping("/")
	public String welcome() {
		return "Hello";
	}
	
	@PreAuthorize("hasRole(T(io.omengye.ws.common.base.Constants).superrole)")
	@RequestMapping("/json")
	public Map<String, String> getJsonFromRequestParam(@RequestParam(value="type", required=false)String type) {
		Map<String, String> map = new HashMap<>();
		if (type == null) {
			map.put("type", "val");
		}
		else {
			map.put("type", type);
		}
		return map;
	}
	
	@GetMapping(value="/jsonbody", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Map<String, String> getJsonFromRequestBody(@RequestBody(required=false)Map<String, String> reqmap) {
		Map<String, String> map = new HashMap<>();
		if (reqmap == null || !reqmap.containsKey("type") || reqmap.get("type") == null) {
			map.put("type", "val");
		}
		else {
			map.put("type", reqmap.get("type"));
		}
		return map;
	}
	
    @GetMapping(value="/g")
    @PreAuthorize("#oauth2.hasScope('read')")
    public HashMap<String, Object> ssltest(Principal principal, 
    		@RequestParam(value="q",required=false)String q,
    		@RequestParam(value="start",required=false)String start,
    		@RequestParam(value="num",required=false)String num)  throws Exception {
    	HashMap<String, Object> result = new HashMap<>();
		if (q==null || q.equals("")) {
			return result;
		}
    	DefaultAsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setAcceptAnyCertificate(true).build();
    	AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(config);
    	try {
    		final List<Response> responses = new CopyOnWriteArrayList<>();
            final CountDownLatch latch = new CountDownLatch(1);
            
            String key = "";
    		String cx= "";
    		
            if (start==null || start.equals("")) {
            	start="1";
            }
            if (num==null || num.equals("")) {
            	num="10";
            }

            String url = "https://www.googleapis.com/customsearch/v1?key="
        			+key+"&cx="+cx+"&q="+q+"&start="+start+"&num="+num;
            httpClientService.sslCallBack(asyncHttpClient, url, responses, latch);
    		latch.await();
    		if (!responses.isEmpty()) {
				for (final Response response : responses) {
					String str = response.getResponseBody();
					 result = searchService.parse(str);
				}
			}
    	}
    	finally {
    		asyncHttpClient.close();
    	}
    	return result;
    }
    
    /**
     * using
     * 
     * curl --progress-bar -k -i -X POST -H "Content-Type: multipart/related;charset=UTF-8" -F \
     * 	data=@"file.txt" "https://domain/upload?access_token=token"
     * 
     * to upload file
     * @param response
     * @param fileRequest
     * @param principal
     * @return
     * @throws IOException
     */
    @PreAuthorize("hasRole(T(io.omengye.ws.common.base.Constants).superrole)")
	@PostMapping("/upload")
	public String postMessage(HttpServletResponse response,
			MultipartHttpServletRequest fileRequest, Principal principal)
			throws IOException {


		// 创建文件保存路径
		FileUtil.createFolder(path);
		
		Iterator<String> iterator = fileRequest.getFileNames();

		while (iterator.hasNext()) {
			String iter = iterator.next();
			MultipartFile multipartFile = fileRequest.getFile(iter);
			if (multipartFile.getSize() > 0) {
				InputStream in = multipartFile.getInputStream();
				String fileName = new String(multipartFile.getOriginalFilename().getBytes("ISO8859-1"),"UTF-8");
				System.out.println(fileName);
				try {
					fileService.saveFile(in, path+fileName);
				} catch (Exception e) {
					return fileName + " 上传失败";
				}
			}
		}
		return "上传成功";
	}
    
	/**
	 * using
	 * 
	 * curl -k -o ~/rename.txt https://domain/download/filename?access_token=token
	 * 
	 * to download file 
	 * 
	 * @param filename
	 * @param response
	 * @param request
	 * @param principal
	 */
    @PreAuthorize("hasRole(T(io.omengye.ws.common.base.Constants).superrole)")
	@RequestMapping("/download/{file}")
	public void download(@PathVariable("file")String filename, HttpServletResponse response,
			HttpServletRequest request, Principal principal) {
		
	    OutputStream os = null;
		try {
			response.setContentType("application/octet-stream"); 
		    String name = URLEncoder.encode(filename, "UTF-8");  
		    response.setHeader("Content-disposition", "attachment;filename="  + name);  
		    os = response.getOutputStream();  
	        IOUtils.copy(new FileInputStream(path+name), os);  
	        os.flush();  
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
	        try {
	        	if (os !=null) {
	        		os.close();
	        	}
			} catch (IOException e) {
				System.out.println("关闭response流错误");
			}  
		}
	}
	
}

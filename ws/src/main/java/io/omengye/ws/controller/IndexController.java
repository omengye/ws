package io.omengye.ws.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
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

import io.omengye.ws.service.HttpClientService;


@RestController
public class IndexController {
	
	@Autowired
	private HttpClientService httpClientService;
	
	@GetMapping("/")
	public String welcome() {
		return "Hello";
	}
	
	@RequestMapping("/jsonparam")
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
	
	@CrossOrigin
    @GetMapping(value="/g")
    @PreAuthorize("#oauth2.hasScope('read')")
    public String ssltest(Principal principal, 
    		@RequestParam(value="q",required=false)String q,
    		@RequestParam(value="start",required=false)String start,
    		@RequestParam(value="num",required=false)String num)  throws Exception {
		String result = "{}";
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
					result=response.getResponseBody();
				}
			}
    	}
    	finally {
    		asyncHttpClient.close();
    	}
    	return result;
    }
    
    
    
	
}

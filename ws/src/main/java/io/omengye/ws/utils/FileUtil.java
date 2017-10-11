package io.omengye.ws.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
	/**
	 * 快速复制文件
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
    public static void fastCopy(final InputStream src, final OutputStream dest) throws IOException {
        final ReadableByteChannel inputChannel = Channels.newChannel(src);
        final WritableByteChannel outputChannel = Channels.newChannel(dest);
        fastCopy(inputChannel, outputChannel);
    }
    
    public static void fastCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        
        while(src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        
        buffer.flip();
        
        while(buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
    
    public static void copyFile(File oldFile, File newFile) throws FileNotFoundException, IOException {
    	if (oldFile==null || newFile==null) {
    			return;
    	}
    	FileInputStream in = new FileInputStream(oldFile);
    	FileOutputStream out = new FileOutputStream(newFile);
    	try {
    		fastCopy(in, out);
    	}
    	finally {
    		if (in!=null) {
    			in.close();
    		}
    		if (out!=null) {
    			out.close();
    		}
    	}
    }
    
    /**
     * 获取文件扩展名
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return "";   
    }   
    
    public static void createFolder(String path) {
    	File file = new File(path);
    	if (!file.exists()) {
    		file.mkdirs();
    	}
    }
    
    /**
     * 打包zip
     * 
     * @param targetFile 需要打包的zip文件
     * @param fileMap 文件名称和文件对应map
     */
    private static void zip(File targetFile, Map<String,File> fileMap) {  
        if (fileMap == null || fileMap.size() == 0) {  
            return;  
        }  
        ZipOutputStream zos = null;  
        byte[] buffer = new byte[10240];
        try {  
            zos = new ZipOutputStream(new FileOutputStream(targetFile));  
            for (String fileName : fileMap.keySet()) {  
                ZipEntry entry = new ZipEntry(fileName);  
                // 设置压缩包的入口  
                zos.putNextEntry(entry); 
                FileInputStream  inputStream = new FileInputStream(fileMap.get(fileName));
                int len;
            	while ((len = inputStream.read(buffer)) > 0) {
            		zos.write(buffer, 0, len);
            	}
            	inputStream.close();
                zos.flush();  
            }  
            zos.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
    
    /**
     * 打包zip
     * 
     * @param zipPath 需要打包的zip文件路径
     * @param zipName 需要打包的zip文件名称
     * @param fileMap 文件名称和文件对应map
     */
    public static File zipFiles(String zipPath, String zipName, Map<String, File> fileMap) {
    	File fileDir = new File(zipPath);  
        if( !fileDir.exists() ){  
        	fileDir.mkdirs();  
        }  
        File f = new File(zipPath+zipName);  
        if( !f.exists() ){  
            try {  
                FileUtil.zip(f, fileMap);
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }
        return f;
    }
    
	/**
	 * 依据文件地址删除文件
	 * @param fileUrl
	 */
	public static void deleteMessageFile(String fileUrl) {
		if (StrUtil.snull(fileUrl)==null) {
			return;
		}
		File file = new File(fileUrl); 
		if(file.isFile()) {
			file.delete();
		}
	}
}

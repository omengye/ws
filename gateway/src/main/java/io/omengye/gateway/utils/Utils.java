package io.omengye.gateway.utils;

import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;


public class Utils {

    public static String justNull(Object o) {
        if (o == null || o.toString().trim().equals("") || o.toString().trim().equalsIgnoreCase("null") || o.toString().trim().equals("undefined")) {
            return null;
        } else {
            return o.toString();
        }
    }

    public static boolean isNotEmpty(Object o) {
        if (justNull(o)==null) {
            return false;
        }
        return true;
    }
    
    public static String justListNull(List<String> o) {
    	if (o==null || o.size()<1) {
    		return null;
    	}
    	else if (o.get(0).trim().equals("") || o.get(0).trim().equalsIgnoreCase("null") || o.get(0).trim().equals("undefined")) {
            return null;
        } else {
            return o.get(0);
        }
    }

    public static boolean isListNotEmpty(List<String> o) {
        if (justListNull(o)==null) {
            return false;
        }
        return true;
    }

    public static String getRealIP(ServerHttpRequest request) {
    	HttpHeaders headers = request.getHeaders();
        if (isListNotEmpty(headers.get("CF-Connecting-IP"))) {
            return justListNull(headers.get("CF-Connecting-IP"));
        }
        if (isListNotEmpty(headers.get("HTTP_CF_CONNECTING_IP"))) {
            return justListNull(headers.get("HTTP_CF_CONNECTING_IP"));
        }
        String Xip = justListNull(headers.get("X-Real-IP"));
        String XFor = justListNull(headers.get("X-Forwarded-For"));
        if(isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)){
            int index = XFor.indexOf(",");
            if(index != -1){
                return XFor.substring(0,index);
            }else{
                return XFor;
            }
        }
        XFor = Xip;
        if(isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)){
            return XFor;
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = justListNull(headers.get("Proxy-Client-IP"));
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = justListNull(headers.get("WL-Proxy-Client-IP"));
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = justListNull(headers.get("HTTP_CLIENT_IP"));
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = justListNull(headers.get("HTTP_X_FORWARDED_FOR"));
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return XFor;
    }

	public static String base64Encode(String str) {
		if (!isNotEmpty(str)) {
			return null;
		}
		byte[] encodedBytes = Base64.getEncoder().encode(str.getBytes());
		return new String(encodedBytes);
	}
	
}

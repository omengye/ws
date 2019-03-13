package io.omengye.userinfo.utils;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

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

    public static String getRealIP(HttpServletRequest request) {
        if (isNotEmpty(request.getHeader("CF-Connecting-IP"))) {
            return request.getHeader("CF-Connecting-IP");
        }
        if (isNotEmpty(request.getHeader("HTTP_CF_CONNECTING_IP"))) {
            return request.getHeader("HTTP_CF_CONNECTING_IP");
        }
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
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
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
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

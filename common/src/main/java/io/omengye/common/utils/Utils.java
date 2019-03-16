package io.omengye.common.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Base64;
import java.util.List;


public class Utils {

    public static final String[] connectips = new String[]{"CF-Connecting-IP", "HTTP_CF_CONNECTING_IP"};

    public static final String[] proxyips = new String[]{"Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

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

    public static String justListNull(List o) {
        if (o==null || o.size()<1) {
            return null;
        }
        else if (!(o.get(0) instanceof String)) {
            return null;
        }
        else if (o.get(0).toString().trim().equals("") || o.get(0).toString().trim().equalsIgnoreCase("null")
                || o.get(0).toString().trim().equals("undefined")) {
            return null;
        } else {
            return o.get(0).toString();
        }
    }


    public static String justObjectNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return justNull(obj);
        }
        else if(obj instanceof List) {
            return justListNull((List)obj);
        }
        return null;
    }

    public static boolean isListNotEmpty(List<String> o) {
        if (justListNull(o)==null) {
            return false;
        }
        return true;
    }


    public static String getXFor(String XFor, String Xip) {
        if(isNotEmpty(XFor) && !"unknown".equalsIgnoreCase(XFor)){
            int index = XFor.indexOf(",");
            if(index != -1){
                return XFor.substring(0,index);
            }else{
                return XFor;
            }
        }
        XFor = Xip;
        if(isNotEmpty(XFor) && !"unknown".equalsIgnoreCase(XFor)){
            return XFor;
        }
        return null;
    }


    public static String base64Encode(String str) {
        if (!isNotEmpty(str)) {
            return null;
        }
        byte[] encodedBytes = Base64.getEncoder().encode(str.getBytes());
        return new String(encodedBytes);
    }

    public static String getRealIP(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        for (String connectip : Utils.connectips) {
            if (Utils.isListNotEmpty(headers.get(connectip))) {
                return Utils.justListNull(headers.get(connectip));
            }
        }

        String Xip = Utils.justListNull(headers.get("X-Real-IP"));
        String XFor = Utils.justListNull(headers.get("X-Forwarded-For"));
        XFor = Utils.getXFor(XFor, Xip);

        for (String proxyip : Utils.proxyips) {
            if (!Utils.isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                XFor = Utils.justListNull(headers.get(proxyip));
            }
            else if (Utils.isNotEmpty(XFor)) {
                return XFor;
            }
        }
        if (!Utils.isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return XFor;
    }

}
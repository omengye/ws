package io.omengye.common.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.List;


public class Utils {

    public static final String[] connectips = new String[]{"CF-Connecting-IP", "HTTP_CF_CONNECTING_IP"};

    public static final String[] proxyips = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

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

        String XFor = "";

        for (String proxyip : Utils.proxyips) {
            XFor = Utils.justListNull(headers.get(proxyip));
            if (Utils.isNotEmpty(XFor) && !"unknown".equalsIgnoreCase(XFor)) {
                return XFor;
            }
        }
        if (!Utils.isNotEmpty(XFor)|| "unknown".equalsIgnoreCase(XFor)) {
            InetSocketAddress addr = request.getRemoteAddress();
            if (addr == null) {
                return XFor;
            }
            XFor = addr.getAddress().getHostAddress();
        }
        return XFor;
    }

    public static String filterQuery(String q, String[] filters) {
        if (justNull(q)==null || filters==null || filters.length<1) {
            return q;
        }
        for (String f : filters) {
            q = q.replace(f, "");
        }
        return q;
    }

}

package io.omengye.common.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Utils {

    private Utils(){}

    private static final String[] CONNECT_IPS = new String[]{"CF-Connecting-IP", "HTTP_CF_CONNECTING_IP"};

    private static final String[] PROXY_IPS = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    private static final String UNKNOWN_IP = "unknown";

    public static String justNull(Object o) {
        return ObjectUtils.isEmpty(o) || StringUtils.isEmpty(o.toString()) ? null : o.toString();
    }

    public static boolean isNotEmpty(Object o) {
        return justNull(o) != null;
    }

    public static String justListNull(List<String> o) {
        if (ObjectUtils.isEmpty(o) || StringUtils.isEmpty(o.get(0))) {
            return null;
        } else {
            return o.get(0);
        }
    }

    public static boolean isListNotEmpty(List<String> o) {
        return justListNull(o) != null;
    }

    public static String base64Encode(String str) {
        if (!isNotEmpty(str)) {
            return null;
        }
        byte[] encodedBytes = Base64.getEncoder().encode(str.getBytes());
        return new String(encodedBytes);
    }

    public static String getServletRealIp(HttpServletRequest request) {
        HttpHeaders httpHeaders = Collections
                .list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(request.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new
                ));
        return getRealIpFromHeaders(httpHeaders, request.getRemoteAddr());
    }

    public static String getServerRealIp(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        InetSocketAddress addr = request.getRemoteAddress();
        String xFor = addr == null ? "" : addr.getAddress().getHostAddress();
        return getRealIpFromHeaders(headers, xFor);
    }

    private static String splitProxyIpByComma(String proxyIp) {
        if (justNull(proxyIp) == null) {
            return UNKNOWN_IP;
        }
        String[] ips = proxyIp.split(",");
        return ips[0].trim();
    }

    private static String getRealIpFromHeaders(HttpHeaders headers, String addr) {
        for (String connectIp : CONNECT_IPS) {
            if (Utils.isListNotEmpty(headers.get(connectIp))) {
                return Utils.justListNull(headers.get(connectIp));
            }
        }
        String xFor = "";
        for (String proxyIp : PROXY_IPS) {
            xFor = Utils.justListNull(headers.get(proxyIp));
            if (Utils.isNotEmpty(xFor) && !UNKNOWN_IP.equalsIgnoreCase(xFor) && !UNKNOWN_IP.equals(splitProxyIpByComma(xFor))) {
                return splitProxyIpByComma(xFor);
            }
        }
        if (!Utils.isNotEmpty(xFor) || UNKNOWN_IP.equalsIgnoreCase(xFor)) {
            xFor = addr;
        }
        return xFor;
    }

    public static String filterQuery(String q, String[] filters) {
        if (justNull(q)==null || ObjectUtils.isEmpty(filters)) {
            return q;
        }
        for (String f : filters) {
            q = q.replace(f, "");
        }
        return q;
    }
}

package io.omengye.userinfo.common;

import io.omengye.common.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class Tools {

    public static String justHttpHeaderList(String XFor) {
        String ip  = "";
        if (Utils.isNotEmpty(XFor) && XFor.contains(",")) {
            String[] ips = XFor.split(",");
            return Utils.justListNull(Arrays.asList(ips));
        }
        else if (Utils.isNotEmpty(XFor)) {
            return XFor;
        }
        return ip;
    }

    public static String getRealIP(HttpServletRequest request) {
        for (String connectip : Utils.connectips) {
            if (Utils.isNotEmpty(request.getHeader(connectip))) {
                return request.getHeader(connectip);
            }
        }

        String XFor = "";

        for (String proxyip : Utils.proxyips) {
            XFor = request.getHeader(proxyip);
            if (Utils.isNotEmpty(XFor) && !"unknown".equalsIgnoreCase(XFor)) {
                return justHttpHeaderList(XFor);
            }
        }
        if (!Utils.isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }

}

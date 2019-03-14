package io.omengye.userinfo.common;

import io.omengye.common.utils.Utils;

import javax.servlet.http.HttpServletRequest;

public class Tools {
    public static String getRealIP(HttpServletRequest request) {
        for (String connectip : Utils.connectips) {
            if (Utils.isNotEmpty(request.getHeader(connectip))) {
                return request.getHeader(connectip);
            }
        }

        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        XFor = Utils.getXFor(XFor, Xip);

        for (String proxyip : Utils.proxyips) {
            if (!Utils.isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
                XFor = request.getHeader(proxyip);
            }
            else if (Utils.isNotEmpty(XFor)) {
                return XFor;
            }
        }
        if (!Utils.isNotEmpty(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }

}

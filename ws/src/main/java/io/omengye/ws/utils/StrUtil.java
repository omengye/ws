package io.omengye.ws.utils;

import java.util.Base64;

public class StrUtil {

	public static String snull(Object o) {
		if (o == null || o.toString().trim().equals("") || o.toString().trim().equalsIgnoreCase("null") || o.toString().trim().equals("undefined")) {
			return null;
		} else {
			return o.toString();
		}
	}
	
	public static String getStr(Object o) {
		if (o == null || o.toString().trim().equals("") || o.toString().trim().equalsIgnoreCase("null") || o.toString().trim().equals("undefined")) {
			return "";
		} else {
			return o.toString();
		}
	}
	
	public static String base64Encode(String str) {
		if (snull(str)==null) {
			return null;
		}
		byte[] encodedBytes = Base64.getEncoder().encode(str.getBytes());
		return new String(encodedBytes);
	}
	
}

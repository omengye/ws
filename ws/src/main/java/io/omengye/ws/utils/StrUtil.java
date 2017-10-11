package io.omengye.ws.utils;

public class StrUtil {

	public static String snull(Object o) {
		if (o == null || o.toString().trim().equals("") || o.toString().trim().equalsIgnoreCase("null") || o.toString().trim().equals("undefined")) {
			return null;
		} else {
			return o.toString();
		}
	}
	
}

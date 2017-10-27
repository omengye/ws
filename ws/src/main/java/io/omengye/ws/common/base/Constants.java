package io.omengye.ws.common.base;

public class Constants {
	
	public static int expireTokenTime = 8*3600;
	
	public static int expireRefreshTokenTime = 12*3600;
	
	public static int expireLoginTime = 60;

	// user setting
	public static String rolePrefix = "ROLE_";
	public static String roleAdmin = "ADMIN";
	public static String roleUser = "USER";
	
	// super user
	public static String superuserip = "ip";
	public static String superusername = "username";
	public static String superuserpwd = "password";
	public static String superrole = rolePrefix+roleAdmin;
	
}

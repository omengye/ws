package io.omengye.userinfo.common.base;

public class Constants {
	
	public static final int expireTokenTime = 9*3600;
	public static final int expireDelayTokenTime = 5*60;
	
	public static final int expireRefreshTokenTime = 12*3600;

	public static final int maxVisit = 5000;

	// user setting
	public static final String rolePrefix = "ROLE_";
	public static final String roleAdmin = "ADMIN";
	public static final String roleUser = "USER";
	
	// super user
	public static final String superuserip = "ip";
	public static final String superusername = "username";
	public static final String superuserpwd = "password";
	public static final String superrole = rolePrefix+roleAdmin;
	
	
}

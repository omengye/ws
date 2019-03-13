package io.omengye.userinfo.entity;

import io.omengye.userinfo.common.base.Constants;
import lombok.Data;

@Data
public class TokenInfo {

	private String token;
	
	private String visittime;
	
	private Long expire;
	
	public TokenInfo(String token, String visittime) {
		this.token = token;
		this.visittime = visittime;
		this.expire = Long.valueOf(Constants.expireTokenTime);
	}
	
}

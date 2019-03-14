package io.omengye.userinfo.entity;

import io.omengye.userinfo.common.base.Constants;
import lombok.Data;

@Data
public class TokenInfo {

	private String token;
	
	private String visittime;
	
	private Long expire;

	private String ip;
	
	public TokenInfo(String token, String visittime, String ip) {
		this.token = token;
		this.visittime = visittime;
		this.ip = ip;
		this.expire = (long) Constants.expireTokenTime;
	}
	
}

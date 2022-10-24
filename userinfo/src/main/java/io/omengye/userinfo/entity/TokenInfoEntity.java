package io.omengye.userinfo.entity;

import io.omengye.userinfo.common.base.Constants;
import lombok.Data;

@Data
public class TokenInfoEntity {

	private String token;

	private String visitTime;

	private Long expire;

	private String ip;

	public TokenInfoEntity(String token, String visitTime, String ip) {
		this.token = token;
		this.visitTime = visitTime;
		this.ip = ip;
		this.expire = (long) Constants.EXPIRE_TOKEN_TIME;
	}

}

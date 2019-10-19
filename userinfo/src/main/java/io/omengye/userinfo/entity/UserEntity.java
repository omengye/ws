package io.omengye.userinfo.entity;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Data;

@Data
@RedisHash("UserEntity")
public class UserEntity implements Serializable {
	
	// userip
	@Id
	private String id;

	private String token;
	
	private String visitTime;

	private Integer vcount = 0;
	
	@TimeToLive
    private Long expirationTime;
	
	public UserEntity() {}
	
	public UserEntity(String id, String visitTime) {
		this.id = id;
		this.visitTime = visitTime;
	}

	public TokenInfo getTokenInfo() {
		return new TokenInfo(token, visitTime, id);
	}

	public UserInfo getUserInfo() {
		return new UserInfo(visitTime, id, vcount);
	}
	
}

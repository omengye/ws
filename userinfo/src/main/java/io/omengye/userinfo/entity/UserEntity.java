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
	@Indexed
	private String id;

	private String token;
	
	private String visittime;
	
	@TimeToLive(unit = TimeUnit.SECONDS)
    private Long expirationTime;
	
	public UserEntity() {};
	
	public UserEntity(String id, String visittime) {
		this.id = id;
		this.visittime = visittime;
	}

	public TokenInfo getTokenInfo() {
		return new TokenInfo(token, visittime);
	}
	
}

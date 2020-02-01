package io.omengye.userinfo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@NoArgsConstructor
@RedisHash("UserEntity")
public class UserEntity implements Serializable {

	// userip
	@Id
	private String id;

	private String token;

	private String visitTime;

	private Integer vCount = 0;

	@TimeToLive
    private Long expirationTime;

	public UserEntity(String id, String visitTime) {
		this.id = id;
		this.visitTime = visitTime;
	}

	public TokenInfo getTokenInfo() {
		return new TokenInfo(token, visitTime, id);
	}

	public UserInfo getUserInfo() {
		return new UserInfo(visitTime, id, vCount);
	}

}

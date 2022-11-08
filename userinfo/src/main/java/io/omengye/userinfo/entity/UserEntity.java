package io.omengye.userinfo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@NoArgsConstructor
@RedisHash(value="UserEntity")
public class UserEntity implements Serializable {

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

	public TokenInfoEntity getTokenInfo() {
		return new TokenInfoEntity(token, visitTime, id);
	}

	public UserInfoEntity getUserInfo() {
		return new UserInfoEntity(visitTime, id, vCount);
	}

}

package io.omengye.userinfo.entity;

import io.omengye.userinfo.common.base.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Data
@AllArgsConstructor
@RedisHash(value="IdRegisteredClientEntity", timeToLive = Constants.EXPIRE_TOKEN_TIME)
public class IdRegisteredClientEntity {

    @Id
    private String id;

    private RegisteredClient registeredClient;

}

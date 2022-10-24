package io.omengye.userinfo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Data
@AllArgsConstructor
@RedisHash("ClientIdRegisteredClientEntity")
public class ClientIdRegisteredClientEntity {

    @Id
    private String id;

    private RegisteredClient registeredClient;

}

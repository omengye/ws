package io.omengye.userinfo.entity;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

@Data
@Builder
@RedisHash("OAuth2AuthorizationEntity")
public class OAuth2AuthorizationEntity {

    @Id
    private String id;

    private OAuth2Authorization oAuth2Authorization;

}

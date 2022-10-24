package io.omengye.userinfo.entity;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash("JWKSetEntity")
public class JWKSetEntity {

    @Id
    private String id;

    private String privateKey;

    private String publicKey;
}

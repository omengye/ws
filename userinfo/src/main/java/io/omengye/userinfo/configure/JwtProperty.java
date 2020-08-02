package io.omengye.userinfo.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    private UserSecurityProperty user;

    private KeyPairProperty keyPair;

}

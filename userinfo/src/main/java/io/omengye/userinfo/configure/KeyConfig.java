package io.omengye.userinfo.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

@Configuration
class KeyConfig {

    @Resource
    private JwtProperty jwtProperty;

    @Bean
    KeyPair keyPair() {
        try {
            String exponent = "65537";
            RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(new BigInteger(jwtProperty.getKeyPair().getModulus()), new BigInteger(exponent));
            RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(new BigInteger(jwtProperty.getKeyPair().getModulus()), new BigInteger(jwtProperty.getKeyPair().getPrivateExponent()));
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return new KeyPair(factory.generatePublic(publicSpec), factory.generatePrivate(privateSpec));
        } catch ( Exception e ) {
            throw new IllegalArgumentException(e);
        }
    }
}

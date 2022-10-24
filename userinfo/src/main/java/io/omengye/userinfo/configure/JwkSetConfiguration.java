package io.omengye.userinfo.configure;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.omengye.userinfo.entity.JWKSetEntity;
import io.omengye.userinfo.repository.JWKSetRepository;
import io.omengye.userinfo.service.RedisOAuth2AuthorizationService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;

import javax.annotation.Resource;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Slf4j
public class JwkSetConfiguration {

	@Resource
	private JWKSetRepository jwkSetRepository;

	private static final String RSA_JWK_SET = "RSA_JWK_SET";

//	@Bean
//	public InMemoryOAuth2AuthorizationService oAuth2AuthorizationService() {
//		return new InMemoryOAuth2AuthorizationService();
//	}

	@SneakyThrows
	private RSAKey loadKey(JWKSetEntity jwkSetEntity) {
		String privateKeyStr = jwkSetEntity.getPrivateKey();
		String publicKeyStr = jwkSetEntity.getPublicKey();

		KeyFactory kf = KeyFactory.getInstance("RSA");

		byte[] privateKeyDecode = Base64.getDecoder().decode(privateKeyStr);
		byte[] publicKeyDecode = Base64.getDecoder().decode(publicKeyStr);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyDecode);
		PrivateKey privateKey = kf.generatePrivate(privateKeySpec);

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyDecode);
		RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(publicKeySpec);

		return new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.build();
	}


	@Bean
	public JWKSource<SecurityContext> jwkSource() throws JOSEException {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);

		String privateKeySTr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		String publicKeySTr = Base64.getEncoder().encodeToString(publicKey.getEncoded());

		jwkSetRepository.save(JWKSetEntity.builder()
						.id(RSA_JWK_SET)
						.privateKey(privateKeySTr)
						.publicKey(publicKeySTr)
						.build());
		return new ImmutableJWKSet<>(jwkSet);
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	public KeyPair generateRsaKey() throws JOSEException {
		JWKSetEntity jwkSetEntity = jwkSetRepository.findById(RSA_JWK_SET).orElse(null);
		if (jwkSetEntity != null) {
			return loadKey(jwkSetEntity).toKeyPair();
		}
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	@Bean
	public ProviderSettings providerSettings() {
		return ProviderSettings.builder().build();
	}
}

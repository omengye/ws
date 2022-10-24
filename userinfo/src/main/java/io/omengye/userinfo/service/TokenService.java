package io.omengye.userinfo.service;

import io.omengye.common.utils.Utils;
import io.omengye.userinfo.common.base.Constants;
import io.omengye.userinfo.entity.TokenInfoEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2TokenFormat;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
public class TokenService {

	private final UserInfoService userInfoService;

//	@Resource
//	private InMemoryOAuth2AuthorizationService oAuth2AuthorizationService;

	@Resource
	private RedisOAuth2AuthorizationService oAuth2AuthorizationService;

	@Resource
	private RegisteredClientRepository registeredClientRepository;

	@Resource
	private JwtEncoder jwtEncoder;

	public TokenService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public TokenInfoEntity genToken(String ip) {
		TokenInfoEntity token = new TokenInfoEntity(null, null, null);
        if (!Utils.isNotEmpty(ip)) {
            return token;
        }
        token = userInfoService.getTokenInfoByIp(ip);
        if (token == null) {
			OAuth2Token oauthToken = postAccessToken(ip);
        	Date expirationDate = Date.from(Objects.requireNonNull(oauthToken.getExpiresAt()));
        	String tokenStr = oauthToken.getTokenValue();
        	String visitTime = userInfoService.saveUser(ip, tokenStr, expirationDate);
            return new TokenInfoEntity(tokenStr, visitTime, ip);
        }
        else {
            return token;
        }
    }

	public OAuth2Token postAccessToken(String ip) {

		String clientId = Utils.base64Encode(ip);

		OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findById(clientId);
		if (oAuth2Authorization == null) {

			RegisteredClient registeredClient = registeredClientRepository.findById(clientId);
			if (registeredClient == null) {
				Instant instant = Instant.now().plusSeconds(Constants.EXPIRE_TOKEN_TIME);
				registeredClient = RegisteredClient
						.withId(clientId)
						.clientId(clientId)
						.authorizationGrantType(AuthorizationGrantType.PASSWORD)
						.clientSecret(clientId)
						.scope(Constants.DEFAULT_CLIENT_CONFIGURATION_AUTHORIZED_SCOPE)
						.clientSecretExpiresAt(instant)
						.tokenSettings(TokenSettings.builder()
								.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
								.accessTokenTimeToLive(Duration.ofSeconds(Constants.EXPIRE_TOKEN_TIME))
								.idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
								.reuseRefreshTokens(false)
								.build())
						.build();

				registeredClientRepository.save(registeredClient);
			}

			OAuth2ClientAuthenticationToken clientPrincipal = new OAuth2ClientAuthenticationToken(registeredClient,
					registeredClient.getClientAuthenticationMethods().iterator().next(), registeredClient.getClientSecret());

			Set<String> authorizedScopes;
			authorizedScopes = Set.of(Constants.DEFAULT_CLIENT_CONFIGURATION_AUTHORIZED_SCOPE);

			OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
					.registeredClient(registeredClient)
					.principal(clientPrincipal)
					.authorizedScopes(authorizedScopes)
					.tokenType(OAuth2TokenType.ACCESS_TOKEN)
					.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
					.build();

//			OAuth2AccessTokenGenerator oAuth2AccessTokenGenerator = new OAuth2AccessTokenGenerator();
//			OAuth2AccessToken oAuth2AccessToken = oAuth2AccessTokenGenerator.generate(tokenContext);

			JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
			Jwt oAuth2AccessToken = jwtGenerator.generate(tokenContext);

			OAuth2Authorization authorization = OAuth2Authorization
					.withRegisteredClient(registeredClient)
					.token(oAuth2AccessToken)
					.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
					.principalName(clientPrincipal.getName())
					.build();

			oAuth2AuthorizationService.save(authorization);

			return oAuth2AccessToken;
		}

		return oAuth2Authorization.getAccessToken().getToken();
	}
}

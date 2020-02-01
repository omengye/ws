package io.omengye.userinfo.service;

import io.omengye.common.utils.Utils;
import io.omengye.userinfo.entity.TokenInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Log4j2
@Service
public class TokenService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private Environment env;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    public TokenInfo genToken(String ip) {
    	TokenInfo token = new TokenInfo(null, null, null);
        if (!Utils.isNotEmpty(ip)) {
            return token;
        }
        token = userInfoService.getTokenInfoByIp(ip);
        if (token == null) {
        	OAuth2AccessToken oauthToken = postAccessToken(ip);
        	Date expirationDate = oauthToken.getExpiration();
        	String tokenStr = oauthToken.getValue();
        	String visitTime = userInfoService.saveUser(ip, tokenStr, expirationDate);
            return new TokenInfo(tokenStr, visitTime, ip);
        }
        else {
            return token;
        }
    }

	public OAuth2AccessToken postAccessToken(String ip)  {
		DefaultOAuth2RequestFactory oAuth2RequestFactory= new DefaultOAuth2RequestFactory(clientDetailsService);

		String clientId = "reader";
		HashMap<String, String> parameters = new HashMap<>(4);
		parameters.put("grant_type", "password");
		parameters.put("username", ip);
		parameters.put("password", Utils.base64Encode(ip));
		parameters.put("scope", "message:read");
		ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
		if (client==null) {
			throw new InvalidClientException("Given client ID does not match authenticated client");
		}
		TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(parameters, client);
		OAuth2Request storedOAuth2Request = oAuth2RequestFactory.createOAuth2Request(client, tokenRequest);
		OAuth2Authentication authentication = new OAuth2Authentication(storedOAuth2Request, null);

		return authorizationServerTokenServices.createAccessToken(authentication);
	}
}

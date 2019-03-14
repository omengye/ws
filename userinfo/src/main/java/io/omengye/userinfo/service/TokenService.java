package io.omengye.userinfo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.omengye.common.utils.Utils;
import io.omengye.userinfo.annotation.CostTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

import io.omengye.userinfo.entity.TokenInfo;
import lombok.extern.log4j.Log4j2;

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
        	//OAuth2AccessToken oauthToken = getTokenFromResponse(ip);
        	Date expirationDate = oauthToken.getExpiration();
        	String tokenStr = oauthToken.getValue();
        	String visittime = userInfoService.saveUser(ip, tokenStr, expirationDate);
            return new TokenInfo(tokenStr, visittime, ip);
        }
        else {
            return token;
        }
    }
	
    @Deprecated
    public OAuth2AccessToken getTokenFromResponse(String ip) {
    	OAuth2AccessToken token = null;
    	try {
	    	String clientIp = env.getProperty("spring.cloud.client.ip-address");
			String clientPort = env.getProperty("port");
	        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
	        resource.setAccessTokenUri("http://"+clientIp+":"+clientPort+"/oauth/token");
	        resource.setClientId("reader");
	        resource.setClientSecret("secret");
	        resource.setGrantType("password");
	        List<String> scopes = new ArrayList<>();
	        scopes.add("message:read");
	        resource.setScope(scopes);
	        resource.setUsername(ip);
	        resource.setPassword(Utils.base64Encode(ip));
	
	        OAuth2RestTemplate oAuthRestTemplate = new OAuth2RestTemplate(resource,
	                new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
	        oAuthRestTemplate.setAccessTokenProvider(new ResourceOwnerPasswordAccessTokenProvider());
	
	        Long start = System.currentTimeMillis();
	        token = oAuthRestTemplate.getAccessToken();
	        log.info("getAccessToken - {} s", (System.currentTimeMillis()-start)/1000F);
	        return token;
    	}
    	catch(Exception e) {
    		log.error("",  e);
    	}
    	return token;
    }


	public OAuth2AccessToken postAccessToken(String ip)  {
		DefaultOAuth2RequestFactory oAuth2RequestFactory= new DefaultOAuth2RequestFactory(clientDetailsService);
		
		String clientId = "reader";
		HashMap<String, String> parameters = new HashMap<>();
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
		OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(authentication);

		return token;
	}
}

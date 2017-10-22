package io.omengye.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import io.omengye.ws.entity.UserEntity;
import io.omengye.ws.service.TokenService;
import io.omengye.ws.service.UserService;

public class CustomTokenEnhancer implements TokenEnhancer {

	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenService tokenService;
	
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    	String username = (String) authentication.getPrincipal();
    	try {
    		UserEntity entity = userService.getUserByName(username);
    		if (entity!=null) {
        		tokenService.addToken(entity.getUserip(), accessToken.getValue());
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}

        return accessToken;
    }

}

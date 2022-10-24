package io.omengye.userinfo.service;


import io.omengye.userinfo.entity.OAuth2AuthorizationEntity;
import io.omengye.userinfo.repository.OAuth2AuthorizationRepository;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final String ACCESS_TOKEN_PREFIX= "ACCESS_TOKEN_";

    private int maxInitializedAuthorizations = 100;

    /*
     * Stores "initialized" (uncompleted) authorizations, where an access token has not yet been granted.
     * This state occurs with the authorization_code grant flow during the user consent step OR
     * when the code is returned in the authorization response but the access token request is not yet initiated.
     */
    private Map<String, OAuth2Authorization> initializedAuthorizations =
            Collections.synchronizedMap(new MaxSizeHashMap<>(this.maxInitializedAuthorizations));

    @Resource
    private OAuth2AuthorizationRepository authorizations;

    RedisOAuth2AuthorizationService(int maxInitializedAuthorizations) {
        this.maxInitializedAuthorizations = maxInitializedAuthorizations;
        this.initializedAuthorizations = Collections.synchronizedMap(new MaxSizeHashMap<>(this.maxInitializedAuthorizations));
    }

    public RedisOAuth2AuthorizationService() {
        this(Collections.emptyList());
    }

    public RedisOAuth2AuthorizationService(OAuth2Authorization... authorizations) {
        this(Arrays.asList(authorizations));
    }

    public RedisOAuth2AuthorizationService(List<OAuth2Authorization> authorizations) {
        Assert.notNull(authorizations, "authorizations cannot be null");
        authorizations.forEach(authorization -> {
            Assert.notNull(authorization, "authorization cannot be null");
            Assert.isTrue(!this.authorizations.existsById(authorization.getId()),
                    "The authorization must be unique. Found duplicate identifier: " + authorization.getId());
            saveToken(authorization);
        });
    }

    private void saveToken(OAuth2Authorization authorization) {
        this.authorizations.save(OAuth2AuthorizationEntity.builder()
                .id(authorization.getId())
                .oAuth2Authorization(authorization)
                .build());
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            String base64Token = Arrays.toString(Base64.getEncoder().encode(accessToken.toString().getBytes()));
            this.authorizations.save(OAuth2AuthorizationEntity.builder()
                    .id(ACCESS_TOKEN_PREFIX + base64Token)
                    .oAuth2Authorization(authorization)
                    .build());
        }
    }

    private void deleteToken(OAuth2Authorization authorization) {
        this.authorizations.deleteById(authorization.getId());
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            String base64Token = Arrays.toString(Base64.getEncoder().encode(accessToken.toString().getBytes()));
            this.authorizations.deleteById(ACCESS_TOKEN_PREFIX + base64Token);
        }
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        if (isComplete(authorization)) {
            saveToken(authorization);
        } else {
            this.initializedAuthorizations.put(authorization.getId(), authorization);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        if (isComplete(authorization)) {
            deleteToken(authorization);
        } else {
            this.initializedAuthorizations.remove(authorization.getId(), authorization);
        }
    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        OAuth2AuthorizationEntity authorizationEntity = this.authorizations.findById(id).orElse(null);
        return authorizationEntity != null ?
                authorizationEntity.getOAuth2Authorization() :
                this.initializedAuthorizations.get(id);
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        String base64Token = Arrays.toString(Base64.getEncoder().encode(token.getBytes()));
        OAuth2AuthorizationEntity oAuth2AuthorizationEntity = this.authorizations.findById(ACCESS_TOKEN_PREFIX + base64Token).orElse(null);
        if (oAuth2AuthorizationEntity != null && hasToken(oAuth2AuthorizationEntity.getOAuth2Authorization(), token, tokenType)) {
            return oAuth2AuthorizationEntity.getOAuth2Authorization();
        }
//        for (OAuth2Authorization authorization : this.authorizations.values()) {
//            if (hasToken(authorization, token, tokenType)) {
//                return authorization;
//            }
//        }
        for (OAuth2Authorization authorization : this.initializedAuthorizations.values()) {
            if (hasToken(authorization, token, tokenType)) {
                return authorization;
            }
        }
        return null;
    }

    private static boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private static boolean hasToken(OAuth2Authorization authorization, String token, @Nullable OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return matchesState(authorization, token) ||
                    matchesAuthorizationCode(authorization, token) ||
                    matchesAccessToken(authorization, token) ||
                    matchesRefreshToken(authorization, token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            return matchesState(authorization, token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            return matchesAuthorizationCode(authorization, token);
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            return matchesAccessToken(authorization, token);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            return matchesRefreshToken(authorization, token);
        }
        return false;
    }

    private static boolean matchesState(OAuth2Authorization authorization, String token) {
        return token.equals(authorization.getAttribute(OAuth2ParameterNames.STATE));
    }

    private static boolean matchesAuthorizationCode(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        return authorizationCode != null && authorizationCode.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesAccessToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        return accessToken != null && accessToken.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesRefreshToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        return refreshToken != null && refreshToken.getToken().getTokenValue().equals(token);
    }

    private static final class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        private MaxSizeHashMap(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > this.maxSize;
        }

    }

}


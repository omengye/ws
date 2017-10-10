package io.omengye.ws.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer // 必须
@EnableResourceServer //必须
public class OAuth2Configure extends AuthorizationServerConfigurerAdapter {
  
	@Autowired
    private UserDetailsService userDetailsService; // 引入security中提供的 UserDetailsService
    @Autowired
    private AuthenticationManager authenticationManager; // 引入security中提供的 AuthenticationManager
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //允许表单认证
                //.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client_id") // 配置默认的client
                .secret("client_secret")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read")
                .autoApprove("read")
                .refreshTokenValiditySeconds(8*3600)
                .accessTokenValiditySeconds(3600);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false); // 禁止重复使用refresh token
    }

    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore(); //使用内存中的 token store
    }
    
}

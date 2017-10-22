package io.omengye.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthProvider customAuthProvider;
	
	@Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser("username")
//        .password("password")
//        .roles("USERS");
        auth.authenticationProvider(customAuthProvider);
    }

    // 将 AuthenticationManager 注册为 bean , 方便配置 oauth server 的时候使用
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    // 将 UserDetailService 注册为 bean , 方便配置 oauth server 的时候使用
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
        .csrf().disable() // oauth server 不需要 csrf 防护
        .httpBasic().disable() // 禁止 basic 认证
        .authorizeRequests()
        .anyRequest().authenticated() //页面需要登录后访问
        ;
	}
	
	@Override
    public void configure(WebSecurity web) throws Exception {
		// 不需要认证的路径
        web.ignoring().antMatchers("/")
        .and()
        .ignoring().antMatchers("/getUserid")
        .and()
        .ignoring().antMatchers("/getToken");
    }

}

package io.omengye.userinfo;

import io.omengye.userinfo.service.TokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

import io.omengye.userinfo.repository.UserRepository;
import io.omengye.userinfo.service.UserInfoService;
import io.omengye.userinfo.utils.Utils;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoApplicationTest {
	
	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private TokenService tokenService;
	
//    @Value("${spring.cloud.client.ip-address}")
//    private String clientIp;
//    
//    @Value("${server.port}")
//    private Integer clientPort;
    
    @Autowired
    private Environment env;
    
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer properties() {
//		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
//		yaml.setResources(new ClassPathResource("application.yml"));
//		propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
//		return propertySourcesPlaceholderConfigurer;
//	}
	
	//@Test
	public void contextLoads() {
	}

	//@Test
	public void testRedisSave()  {
		
//		try {
//			long start = System.currentTimeMillis();
//			userInfoService.saveUser("1", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWJqZWN0IiwiZXhwIjoxNTQ1Mjc1OTU5LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVJTIl0sImp0aSI6IjViZDE3ZThjLThkMGMtNGUwMS05MWJlLWRkOGZkYzk0Nzk0MyIsImNsaWVudF9pZCI6InJlYWRlciIsInNjb3BlIjpbIm1lc3NhZ2U6cmVhZCJdfQ.hsIhIE56w8zB906BuNq0KAuD2skJS_XoPUkOjBkQ7C5nAgriK2GiNDnFP2lfEyLe8HVLC5reSVxjqy4gguAR33mv0qJu4QEv8xpyz3KGOZpvLFNGJyG5mC54vFoQyJPkkO6LB9igZw4UP_NIdMOrzorowpk4CccjejD4jjiUkeAE2OxmWvq6cX9sIPPayI1zpmbFbrBbdbHKG0S4KdHUGcGmXCu8dI_2vt2DfaQgsl-EWiLdsX3J-bOj3CfJ8FTnfBDPXolBowT3sGKG7Na6n0AXCP_fsISyN7xtm3wMmXHXTZOznQSiLKSPG2Vu3EMrDEqE2-Z1-ZWVGJ5iylEVBA",3L);
//			//Thread.sleep(4*1000);
//			
//			System.out.println(System.currentTimeMillis() - start);
//			start = System.currentTimeMillis();
//			
//			String token = userInfoService.getTokenByIp("1");
//			System.out.println(token);
//			System.out.println(System.currentTimeMillis() - start);
//			
//			/**
//			 * 
//			 */
//			start = System.currentTimeMillis();
//			userInfoService.saveUser("2", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWJqZWN0IiwiZXhwIjoxNTQ1Mjc1OTU5LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVJTIl0sImp0aSI6IjViZDE3ZThjLThkMGMtNGUwMS05MWJlLWRkOGZkYzk0Nzk0MyIsImNsaWVudF9pZCI6InJlYWRlciIsInNjb3BlIjpbIm1lc3NhZ2U6cmVhZCJdfQ.hsIhIE56w8zB906BuNq0KAuD2skJS_XoPUkOjBkQ7C5nAgriK2GiNDnFP2lfEyLe8HVLC5reSVxjqy4gguAR33mv0qJu4QEv8xpyz3KGOZpvLFNGJyG5mC54vFoQyJPkkO6LB9igZw4UP_NIdMOrzorowpk4CccjejD4jjiUkeAE2OxmWvq6cX9sIPPayI1zpmbFbrBbdbHKG0S4KdHUGcGmXCu8dI_2vt2DfaQgsl-EWiLdsX3J-bOj3CfJ8FTnfBDPXolBowT3sGKG7Na6n0AXCP_fsISyN7xtm3wMmXHXTZOznQSiLKSPG2Vu3EMrDEqE2-Z1-ZWVGJ5iylEVBA",3L);
//			//Thread.sleep(4*1000);
//			
//			System.out.println(System.currentTimeMillis() - start);
//			start = System.currentTimeMillis();
//			
//			token = userInfoService.getTokenByIp("2");
//			System.out.println(token);
//			System.out.println(System.currentTimeMillis() - start);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	@Test
	public void testToken() {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		String clientIp = env.getProperty("spring.cloud.client.ip-address");
		String clientPort = env.getProperty("port");
		resource.setAccessTokenUri("http://"+clientIp+":"+clientPort+"/oauth/token");
		resource.setClientId("reader");
		resource.setClientSecret("secret");
		resource.setGrantType("password");
		List<String> scopes = new ArrayList<>();
		scopes.add("message:read");
		resource.setScope(scopes);
		String ip = "0:0:0:0:0:0:0:1";
		resource.setUsername(ip);
		resource.setPassword(Utils.base64Encode(ip));

        OAuth2RestTemplate oAuthRestTemplate = new OAuth2RestTemplate(resource,
                new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
        oAuthRestTemplate.setAccessTokenProvider(new ResourceOwnerPasswordAccessTokenProvider());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

		OAuth2AccessToken otoken = oAuthRestTemplate.getAccessToken();
		System.out.println(oAuthRestTemplate.getResource());
		System.out.println(oAuthRestTemplate.getOAuth2ClientContext());
		System.out.println(otoken);
	}


}

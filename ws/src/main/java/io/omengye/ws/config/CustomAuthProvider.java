package io.omengye.ws.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import io.omengye.ws.entity.UserEntity;
import io.omengye.ws.service.UserService;
import io.omengye.ws.utils.StrUtil;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

	@Autowired
	private UserService userService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		UserEntity user = null;
		try {
			if (StrUtil.snull(username) != null) {
				user = userService.getUser(username);
			}
		} catch (Exception e) {
//			System.out.println("查询用户错误");
//			e.printStackTrace();
		}

		if (user == null) {
			// 用户名错误
			throw new UsernameNotFoundException("用户不存在");
		}

		List<String> roles = user.getRoles();
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}

		if (!password.equals(user.getPassword())) {
			// 密码错误
			throw new BadCredentialsException("密码错误");
		}

		return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}

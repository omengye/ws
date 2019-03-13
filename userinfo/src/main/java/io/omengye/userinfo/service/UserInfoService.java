package io.omengye.userinfo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import io.omengye.userinfo.common.base.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.omengye.userinfo.annotation.CostTime;
import io.omengye.userinfo.entity.TokenInfo;
import io.omengye.userinfo.entity.UserEntity;
import io.omengye.userinfo.repository.UserRepository;
import io.omengye.userinfo.utils.Utils;

@Service
public class UserInfoService implements UserDetailsService {


	@Autowired
	private UserRepository userRepository;
	
	public UserEntity getUserByIp(String userIp) {
		if (!userRepository.existsById(userIp)) {
			return null;
		}
		Optional<UserEntity> userEntity = userRepository.findById(userIp);
		if (Optional.empty().equals(userEntity)) {
			return null;
		}
		return userEntity.get();
	}
	
	public String getTokenByIp(String userIp) {
		UserEntity user = getUserByIp(userIp);
		if(user==null) {
			return null;
		}
		return user.getToken();
	}
	
	public TokenInfo getTokenInfoByIp(String userIp) {
		UserEntity user = getUserByIp(userIp);
		if(user==null) {
			return null;
		}
		return user.getTokenInfo();
	}
	
	public String saveUser(String userIp, String token, Date expirationDate) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String visittime = sf.format(now);
		UserEntity user = new UserEntity(userIp, visittime);
		user.setToken(token);
		Long expire = (expirationDate.getTime() - now.getTime())/1000 - Constants.expireDelayTokenTime;
		user.setExpirationTime(expire);
		userRepository.save(user);
		return visittime;
	}

	// username -> ip
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String roles = "USERS";
		UserBuilder userBuilder = User.builder();
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		String password = Utils.base64Encode(username);

		UserDetails build = userBuilder
				.passwordEncoder(encoder::encode)
				.username(username)
				.password(password)
				.roles(roles)
				.build();
		return build;
	}
	
}

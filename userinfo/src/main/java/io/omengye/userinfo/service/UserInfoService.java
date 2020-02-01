package io.omengye.userinfo.service;

import io.omengye.common.utils.Utils;
import io.omengye.userinfo.common.base.Constants;
import io.omengye.userinfo.entity.TokenInfo;
import io.omengye.userinfo.entity.UserEntity;
import io.omengye.userinfo.entity.UserInfo;
import io.omengye.userinfo.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserInfoService implements UserDetailsService {


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${Jwt.user.name}")
	private String jwtUser;

	@Value("${Jwt.user.password}")
	private String jwtPassword;

	public boolean notValidUser(String user, String password) {
		return user==null || password==null || user.length()!=jwtUser.length() || password.length()!=jwtPassword.length()
				|| !jwtUser.equals(user) || !jwtPassword.equals(password);
	}

	public List<UserInfo> getAllUser() {
		List<UserInfo> list = new ArrayList<>();
		userRepository.findAll().forEach(i->{
			if (i != null) {
				list.add(i.getUserInfo());
			}
		});

		list.sort((o1, o2) -> o2.getCount() - o1.getCount());
		return list;
	}

	public UserEntity getUserByIp(String userIp) {
		if (!userRepository.existsById(userIp)) {
			return null;
		}
		Optional<UserEntity> userEntity = userRepository.findById(userIp);
		if (Optional.empty().equals(userEntity)) {
			return null;
		}
		return userEntity.orElse(null);
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
		Long expire = (expirationDate.getTime() - now.getTime())/1000 - Constants.EXPIRE_DELAY_TOKEN_TIME;
		user.setExpirationTime(expire);
		userRepository.save(user);
		return visittime;
	}

	public boolean addVisitCount(String userIp) {

		UserEntity user = getUserByIp(userIp);
		if(user == null) {
			return false;
		}
		Integer count = user.getVCount();
		if (count == null) {
			user.setVCount(1);
		}
		else if (count > Constants.MAX_VISIT) {
			return false;
		}
		else {
			user.setVCount(count+1);
		}
		userRepository.save(user);
		return true;
	}

	// username -> ip
	@Override
	public UserDetails loadUserByUsername(String username) {
		String roles = "USERS";
		UserBuilder userBuilder = User.builder();

		String password;
		if (jwtUser.equals(username)) {
			password = jwtPassword;
		}
		else {
			password = Utils.base64Encode(username);
		}

		if (StringUtils.isEmpty(password)) {
			throw new UsernameNotFoundException("unrecognised username");
		}

		return userBuilder
				.passwordEncoder(passwordEncoder::encode)
				.username(username)
				.password(password)
				.roles(roles)
				.build();
	}

}

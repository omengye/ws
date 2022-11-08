package io.omengye.userinfo.service;

import io.omengye.common.utils.Utils;
import io.omengye.userinfo.common.base.Constants;
import io.omengye.userinfo.entity.TokenInfoEntity;
import io.omengye.userinfo.entity.UserEntity;
import io.omengye.userinfo.entity.UserInfoEntity;
import io.omengye.userinfo.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserInfoService {

	@Resource
	private UserRepository userRepository;

	public List<UserInfoEntity> getAllUser() {
		List<UserInfoEntity> list = new ArrayList<>();
		userRepository.findAll().forEach(i->{
			if (i != null) {
				list.add(i.getUserInfo());
			}
		});

		list.sort((o1, o2) -> o2.getCount() - o1.getCount());
		return list;
	}

	public UserEntity getUserByIp(String userIp) {
		String hashIp = Utils.base64Encode(userIp);
		if (Strings.isEmpty(hashIp) || !userRepository.existsById(hashIp)) {
			return null;
		}
		Optional<UserEntity> userEntity = userRepository.findById(hashIp);
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

	public TokenInfoEntity getTokenInfoByIp(String userIp) {
		UserEntity user = getUserByIp(userIp);
		if(user==null) {
			return null;
		}
		return user.getTokenInfo();
	}

	public String saveUser(String userIp, String token, Date expirationDate) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String visitTime = sf.format(now);
		UserEntity user = new UserEntity(Utils.base64Encode(userIp), visitTime);
		user.setToken(token);
		Long expire = (expirationDate.getTime() - now.getTime())/1000 - Constants.EXPIRE_DELAY_TOKEN_TIME;
		user.setExpirationTime(expire);
		userRepository.save(user);
		return visitTime;
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

}

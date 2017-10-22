package io.omengye.ws.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.omengye.ws.common.base.Constants;
import io.omengye.ws.entity.UserEntity;
import io.omengye.ws.utils.StrUtil;

@Service
public class UserService {
	
	private final static String userNotFound = "USER_NOT_FOUND";
	
	private static LoadingCache<String, UserEntity> userIpCache = CacheBuilder.newBuilder()
			.expireAfterWrite(Constants.expireLoginTime, TimeUnit.SECONDS).build(new CacheLoader<String, UserEntity>() {
				@Override
				public UserEntity load(String userip) throws Exception {
					throw new Exception(userNotFound);
				}
			});
	
	private static LoadingCache<String, UserEntity> userNameCache = CacheBuilder.newBuilder()
			.expireAfterWrite(Constants.expireLoginTime, TimeUnit.SECONDS).build(new CacheLoader<String, UserEntity>() {
				@Override
				public UserEntity load(String userip) throws Exception {
					throw new Exception(userNotFound);
				}
			});
	
	public void addUser(String userip) {
		if (StrUtil.snull(userip)==null) {
			return;
		}
		//
		String username = UUID.randomUUID().toString();
		String password = StrUtil.base64Encode(username);
		userIpCache.put(userip, new UserEntity(userip, username, password));
		userNameCache.put(username, new UserEntity(userip, username, password));
	}
	
	public UserEntity getUserByIp(String userip) throws Exception {
		UserEntity entity = null;
		try {
			entity = userIpCache.get(userip);
		}
		catch(Exception ex) {
			if (StrUtil.snull(ex.getCause().getMessage())!=null 
					&& ex.getCause().getMessage().equals(userNotFound)) {
				return entity;
			}
			throw ex;
		}
		return entity;
	}
	
	public UserEntity getUserByName(String username) throws Exception {
		UserEntity entity = null;
		try {
			entity = userNameCache.get(username);
		}
		catch(Exception ex) {
			if (StrUtil.snull(ex.getCause().getMessage())!=null 
					&& ex.getCause().getMessage().equals(userNotFound)) {
				return entity;
			}
			throw ex;
		}
		return entity;
	}
	
}

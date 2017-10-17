package io.omengye.ws.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.omengye.ws.entity.UserEntity;
import io.omengye.ws.utils.StrUtil;

@Service
public class UserService {
	
	private final static String userNotFound = "USER_NOT_FOUND";
	
	private static ConcurrentHashMap<String, UserEntity> userLocal = new ConcurrentHashMap<>();
	
	private static LoadingCache<String, UserEntity> userCache = CacheBuilder.newBuilder()
			.expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, UserEntity>() {
				@Override
				public UserEntity load(String username) throws Exception {
					throw new Exception(userNotFound);
				}
			});
	
	static {
		// 创建可认证用户
		userLocal.put("username", new UserEntity("username", "password"));
	}
	
	public void addUser(String username) {
		if (StrUtil.snull(username)==null) {
			return;
		}
		String password = StrUtil.base64Encode(username);
		userCache.put(username, new UserEntity(username, password));
	}
	
	public UserEntity getUser(String username) throws Exception {
		UserEntity entity = null;
		try {
			entity = userCache.get(username);
		}
		catch(Exception ex) {
			if (StrUtil.snull(ex.getCause().getMessage())!=null 
					&& ex.getCause().getMessage().equals(userNotFound)) {
				return userLocal.get(username);
			}
			throw ex;
		}
		
		return entity;
	}
	
}

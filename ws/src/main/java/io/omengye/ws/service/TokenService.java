package io.omengye.ws.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.omengye.ws.common.base.Constants;
import io.omengye.ws.entity.UserEntity;
import io.omengye.ws.utils.StrUtil;

@Service
public class TokenService {
	private final static String tokenNotFound = "TOKEN_NOT_FOUND";
	
	private static LoadingCache<String, String> tokenCache = CacheBuilder.newBuilder()
			.expireAfterWrite(Constants.expireTokenTime, TimeUnit.SECONDS).build(new CacheLoader<String, String>() {
				@Override
				public String load(String userip) throws Exception {
					throw new Exception(tokenNotFound);
				}
			});
	
	public void addToken(String userip, String token) {
		if (StrUtil.snull(userip)==null) {
			return;
		}
		tokenCache.put(userip, token);
	}
	
	public String getToken(String userip) throws Exception {
		String token = null;
		try {
			token = tokenCache.get(userip);
		}
		catch(Exception ex) {
			if (StrUtil.snull(ex.getCause().getMessage())!=null 
					&& ex.getCause().getMessage().equals(tokenNotFound)) {
				return null;
			}
			throw ex;
		}
		return token;
	}
	
}

package io.omengye.userinfo.controller;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.omengye.common.utils.Utils;
import io.omengye.userinfo.common.Tools;
import io.omengye.userinfo.entity.TokenInfo;
import io.omengye.userinfo.entity.UserEntity;
import io.omengye.userinfo.entity.UserInfo;
import io.omengye.userinfo.service.TokenService;
import io.omengye.userinfo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import javax.servlet.http.HttpServletRequest;

@RestController
public class JwksController {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private KeyPair keyPair;


	@GetMapping("/.well-known/jwks.json")
	public Map<String, Object> getKey(@RequestParam(required = false) String user,
									  @RequestParam(required = false) String password) {
		if (userInfoService.notValidUser(user, password)) {
			return new HashMap<>();
		}
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAKey key = new RSAKey.Builder(publicKey).build();
		return new JWKSet(key).toJSONObject();
	}


	@GetMapping("/genToken")
	public TokenInfo getToken(HttpServletRequest req) {
		String userip = Tools.getRealIP(req);
		if (!Utils.isNotEmpty(userip)) {
			return new TokenInfo(null, null, null);
		}
		return tokenService.genToken(userip);
	}

	@GetMapping("/users")
	public List<UserInfo> getAllUser() {
		return userInfoService.getAllUser();
	}

	@GetMapping("/visit")
	public Map<String, Boolean> addVisitCount(@RequestParam String userip) {
		boolean flag = false;
		Map<String, Boolean> res = new HashMap<>();
		if (Utils.isNotEmpty(userip)) {
			flag = userInfoService.addVisitCount(userip);
		}
		res.put("flag", flag);
		return res;
	}

}

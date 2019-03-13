package io.omengye.userinfo.controller;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import io.omengye.userinfo.entity.TokenInfo;
import io.omengye.userinfo.service.TokenService;
import io.omengye.userinfo.service.UserInfoService;
import io.omengye.userinfo.utils.Utils;
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
	private KeyPair keyPair;

	@Value("${Jwt.user.name}")
	private String jwtUser;

	@Value("${Jwt.user.password}")
	private String jwtPassword;


	@GetMapping("/.well-known/jwks.json")
	public Map<String, Object> getKey(@RequestParam String user, @RequestParam String password) {
		if (user==null || password==null || user.length()!=jwtUser.length() || password.length()!=jwtPassword.length()
			|| !jwtUser.equals(user) || !jwtPassword.equals(password)) {
			return new HashMap<>();
		}
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAKey key = new RSAKey.Builder(publicKey).build();
		return new JWKSet(key).toJSONObject();
	}


	@GetMapping("/genToken")
	public TokenInfo getToken(HttpServletRequest req) {
		String userip = Utils.getRealIP(req);
		if (!Utils.isNotEmpty(userip)) {
			return new TokenInfo(null, null);
		}
		return tokenService.genToken(userip);
	}

}

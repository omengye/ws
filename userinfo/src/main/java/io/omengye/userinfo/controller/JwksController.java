package io.omengye.userinfo.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.omengye.common.utils.Utils;
import io.omengye.common.utils.constants.Constants;
import io.omengye.userinfo.entity.TokenInfo;
import io.omengye.userinfo.entity.UserInfo;
import io.omengye.userinfo.service.TokenService;
import io.omengye.userinfo.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class JwksController {

	private TokenService tokenService;
	private UserInfoService userInfoService;
	private KeyPair keyPair;

	public JwksController(TokenService tokenService, UserInfoService userInfoService, KeyPair keyPair) {
		this.tokenService = tokenService;
		this.userInfoService = userInfoService;
		this.keyPair = keyPair;
	}

	@GetMapping("/token")
	public TokenInfo getToken(HttpServletRequest req) {
		String userIp = Utils.getServletRealIp(req);
		if (!Utils.isNotEmpty(userIp)) {
			return new TokenInfo(null, null, null);
		}
		return tokenService.genToken(userIp);
	}

	@GetMapping("/.well-known/jwks.json")
	public Map<String, Object> getKey() {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAKey key = new RSAKey.Builder(publicKey).build();
		return new JWKSet(key).toJSONObject();
	}

	@GetMapping("/headers")
	public Map<String, Object> hello(HttpServletRequest request) {
		Map<String, Object> res = new HashMap<>(16);
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String key = headers.nextElement();
			String value = request.getHeader(key);
			res.put(key, value);
		}
		return res;
	}

	@GetMapping("/users")
	public List<UserInfo> getAllUser() {
		return userInfoService.getAllUser();
	}

	@GetMapping("/visit")
	public Map<String, Boolean> addVisitCount(@RequestParam("userIp") String userIp) {
		boolean flag = false;
		Map<String, Boolean> res = new HashMap<>(1);
		if (Utils.isNotEmpty(userIp)) {
			flag = userInfoService.addVisitCount(userIp);
		}
		res.put(Constants.RESULT_FLAG, flag);
		return res;
	}

}

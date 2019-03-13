package io.omengye.userinfo.controller;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import io.omengye.userinfo.entity.TokenInfo;
import io.omengye.userinfo.service.TokenService;
import io.omengye.userinfo.service.UserInfoService;
import io.omengye.userinfo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import javax.servlet.http.HttpServletRequest;

@RestController
public class JwksController {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private KeyPair keyPair;

	public JwksController(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	@GetMapping("/.well-known/jwks.json")
	@ResponseBody
	public Map<String, Object> getKey(Principal principal) {
		RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();
		RSAKey key = new RSAKey.Builder(publicKey).build();
		return new JWKSet(key).toJSONObject();
	}


	@GetMapping("/genToken")
	@ResponseBody
	public TokenInfo getToken(HttpServletRequest req) {
		String userip = Utils.getRealIP(req);
		if (!Utils.isNotEmpty(userip)) {
			return new TokenInfo(null, null);
		}
		return tokenService.genToken(userip);
	}

}

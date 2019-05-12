package io.omengye.gcs.controller;

import java.util.*;

import io.omengye.common.utils.Utils;
import io.omengye.gcs.entity.GCResponseEntity;
import io.omengye.gcs.entity.ReqEntity;
import io.omengye.gcs.service.UserInfoService;
import io.omengye.gcs.service.WebClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import io.omengye.gcs.entity.GCEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Log4j2
@RestController
public class IndexController {

	@Autowired
	private WebClientService webClientService;

	@Autowired
	private UserInfoService userInfoService;

	@GetMapping(value="/api/g", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<GCResponseEntity> g(@Valid ReqEntity req,
									ServerWebExchange exchange) {
		String userip = Utils.getRealIP(exchange.getRequest());
		Map<String, Boolean> flag = userInfoService.addVisitCount(userip);
		if (!flag.get("flag")) {
			return Mono.error(new AuthenticationServiceException("This User: " +  (userip==null?"":userip) + " Not Exist!"));
		}

		Mono<GCEntity> result = webClientService.getSearchReponse(req);

		return result.map(t -> t.getGCResponseEntity());
	}
	
	@GetMapping(value="/api/suggest", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> suggest(@RequestParam(value="q",required=false)String q) {
		String result = "[]";
		if (q==null || q.equals("")) {
			return Mono.just(result);
		}
		String url = "https://www.google.com/complete/search?client=psy-ab&hl=zh-CN&gs_rn=64&gs_ri=psy-ab&cp=10"
				+ "&gs_id=nv&q="+q+"&xhr=t";
		Mono<String> res = webClientService.getReponse(url, "", result, String.class);

		return res;
	}
	
	@GetMapping("/")
	public String index(@AuthenticationPrincipal Jwt jwt) {
		return String.format("Hello, %s!", jwt.getSubject());
	}

	@GetMapping("/api/test")
	public String test(@RequestParam String id) {
		return id;
	}

	
}

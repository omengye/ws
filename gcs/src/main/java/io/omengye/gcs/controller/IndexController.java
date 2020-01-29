package io.omengye.gcs.controller;

import io.omengye.common.utils.Utils;
import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GCResponseEntity;
import io.omengye.gcs.entity.ReqEntity;
import io.omengye.gcs.feign.UserInfoService;
import io.omengye.gcs.service.WebClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

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
		String userIp = Utils.getServerRealIp(exchange.getRequest());
		Map<String, Boolean> flag = userInfoService.addVisitCount(userIp);
		if (flag.get("flag") == null || !flag.get("flag")) {
			return Mono.error(new AuthenticationServiceException("This User: " +  (StringUtils.isEmpty(userIp)?"":userIp) + " Not Exist!"));
		}

		Mono<GCEntity> result = webClientService.getSearchReponse(req);

		return result.map(GCEntity::getGCResponseEntity);
	}

	@GetMapping(value="/api/suggest", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> suggest(@RequestParam(value="q",required=false)String q) {
		String result = "[]";
		if (StringUtils.isEmpty(q)) {
			return Mono.just(result);
		}
		String url = "https://www.google.com/complete/search?client=psy-ab&hl=zh-CN&gs_rn=64&gs_ri=psy-ab&cp=10"
				+ "&gs_id=nv&q="+q+"&xhr=t";

		return webClientService.getReponse(url, "", result, String.class);
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

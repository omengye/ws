package io.omengye.gcs.controller;

import io.omengye.common.utils.Utils;
import io.omengye.common.utils.constants.Constants;
import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GCResponseEntity;
import io.omengye.gcs.entity.ReqEntity;
import io.omengye.gcs.feign.UserInfoFeignService;
import io.omengye.gcs.service.ChooseItemService;
import io.omengye.gcs.service.WebClientService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Log4j2
@RestController
public class IndexController {

	private final ChooseItemService chooseItemService;

	private final WebClientService webClientService;

	private final UserInfoFeignService userInfoService;

	public IndexController(ChooseItemService chooseItemService, WebClientService webClientService,
						   UserInfoFeignService userInfoService) {
		this.chooseItemService = chooseItemService;
		this.webClientService = webClientService;
		this.userInfoService = userInfoService;
	}

	@GetMapping(value="/g", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<GCResponseEntity> g(@Valid ReqEntity req,
									ServerWebExchange exchange) {
		String userIp = Utils.getServerRealIp(exchange.getRequest());
		Map<String, Boolean> flag = userInfoService.addVisitCount(userIp);
		if (flag.get(Constants.RESULT_FLAG) == null || !flag.get(Constants.RESULT_FLAG)) {
			return Mono.error(new AuthenticationServiceException("This User: " +  (StringUtils.isEmpty(userIp)?"":userIp) + " Not Exist!"));
		}

		Mono<GCEntity> result = webClientService.getSearchResponse(req);

		return result.map(GCEntity::getGCResponseEntity);
	}

	@GetMapping(value="/suggest", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> suggest(@RequestParam(value="q",required=false)String q) {
		String result = "[]";
		if (StringUtils.isEmpty(q)) {
			return Mono.just(result);
		}
		String url = "https://www.google.com/complete/search?client=psy-ab&hl=zh-CN&gs_rn=64&gs_ri=psy-ab&cp=10"
				+ "&gs_id=nv&q="+q+"&xhr=t";

		return chooseItemService.getResponse(url, "", result, String.class);
	}


}

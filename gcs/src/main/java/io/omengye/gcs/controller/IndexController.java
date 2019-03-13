package io.omengye.gcs.controller;

import java.util.*;

import io.omengye.gcs.entity.GCResponseEntity;
import io.omengye.gcs.service.WebClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.omengye.gcs.entity.GCEntity;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@RestController
public class IndexController {

	@Autowired
	private WebClientService webClientService;

	@GetMapping(value="/api/g", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<GCResponseEntity> g(@RequestParam(value="q",required=false)String q,
									@RequestParam(value="start",required=false)String start,
									@RequestParam(value="num",required=false)String num) {

		//Mono<GCEntity> result = webClientService.getReponse("http://127.0.0.1:8443/gc/test", "", GCEntity.class);
		if (start==null || start.equals("")) {
			start="1";
		}
		if (num==null || num.equals("")) {
			num="10";
		}
		Mono<GCEntity> result = webClientService.getSearchReponse(q, start, num);

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

package io.omengye.ws.controller;

import java.nio.charset.Charset;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class IndexController {
	@GetMapping("/")
	public String welcome() {
		return "Hello World";
	}
	
	@GetMapping("/json")
	public Map<String, String> json(@RequestParam(value="id",required=false)String id) {
		Map<String, String> map = new HashMap<>();
		if (id == null) {
			map.put("name", "val");
		}
		else {
			map.put("name", id);
		}
		return map;
	}
	
	@GetMapping("/async")
	public Mono<String> async() {
		return Mono.just("hello").delaySubscription(Duration.ofSeconds(20));
	}
	
	@GetMapping("/resp")
	public Flux<Map> getResponse() {
		WebClient client = WebClient.create("https://www.googleapis.com");
		WebClient.RequestBodySpec uri = client.method(HttpMethod.POST).uri("/customsearch/v1");
		
		LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", "test");
        
		BodyInserter<MultiValueMap<String, ?>, ClientHttpRequest> inserter = BodyInserters.fromMultipartData(map);
		WebClient.ResponseSpec response = uri.body(inserter)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
	        .acceptCharset(Charset.forName("UTF-8"))
	        .ifNoneMatch("*")
	        .ifModifiedSince(ZonedDateTime.now())
	        .retrieve();
		
		return response.bodyToFlux(Map.class);
	}
	
	@GetMapping("/res/{q}")
	public Flux<Map> response(@PathVariable String q) {
		WebClient client = WebClient.create("https://www.googleapis.com");
		WebClient.RequestBodySpec uri = client.method(HttpMethod.GET)
				.uri("/customsearch/v1").accept(MediaType.APPLICATION_JSON);
        
		WebClient.ResponseSpec response = uri
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header("q", q)
	        .accept(MediaType.APPLICATION_JSON)
	        .acceptCharset(Charset.forName("UTF-8"))
	        .ifNoneMatch("*")
	        .ifModifiedSince(ZonedDateTime.now())
	        .retrieve();
		
		return response.bodyToFlux(Map.class);
	}
	
}

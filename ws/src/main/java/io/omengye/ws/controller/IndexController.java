package io.omengye.ws.controller;

import java.time.Duration;

import javax.xml.ws.Response;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import io.omengye.ws.entity.Entity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class IndexController {
	@GetMapping("/")
	public String welcome() {
		return "Hello World";
	}
	
	@GetMapping("/async")
	public Mono<String> async() {
		return Mono.just("hello").delaySubscription(Duration.ofSeconds(20));
	}
	
	
}

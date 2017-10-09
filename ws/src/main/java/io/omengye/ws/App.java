package io.omengye.ws;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
public class App {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class);
	}
}

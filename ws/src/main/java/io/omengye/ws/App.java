package io.omengye.ws;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App {
	
	public static ApplicationContext ctx;
	
	public static void main(String[] args) {
		ctx = SpringApplication.run(App.class, args);
	}
}

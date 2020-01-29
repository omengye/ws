package io.omengye.gcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = {"io.omengye.gcs.feign"})
public class GcsApplication {

	public static void main(String[] args) {
	     SpringApplication.run(GcsApplication.class, args);
	}

}


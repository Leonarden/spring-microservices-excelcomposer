package com.dev.spring.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringMicroserviceExcelGenerator {

	
	public static void main(String[] args) {
		SpringApplication.run(SpringMicroserviceExcelGenerator.class, args);
	}
}

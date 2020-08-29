package com.dev.spring.filedata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.dev.spring.filedata")
@EnableDiscoveryClient
public class SpringBootMicroserviceFileDataFacadeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMicroserviceFileDataFacadeApplication.class, args);
	}
}

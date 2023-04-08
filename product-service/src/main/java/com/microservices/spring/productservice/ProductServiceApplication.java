package com.microservices.spring.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication(scanBasePackages = { "com.microservices.spring.productservice",
		"com.microservices.spring.common" })
@EnableMongoAuditing
@OpenAPIDefinition
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}

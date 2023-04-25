package com.microservices.spring.apigatewayservice;

import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PreDestroy;

@SpringBootApplication(scanBasePackages = { "com.microservices.spring.apigatewayservice",
		"com.microservices.spring.common" })
public class ApiGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayServiceApplication.class, args);
	}

	@PreDestroy
	public void cleanUp() {
		Set<Thread> threads = Thread.getAllStackTraces().keySet();

		for (Thread t : threads) {
			// Fix: "Illegal access: this web application instance has been stopped
			// already."
			t.setContextClassLoader(null);

			// System.out.println("--------------------------------------------------");
			// System.out.printf("%s | %s | %d | %s | %s\n", t.getName(),
			// t.getState(), t.getPriority(), t.isDaemon(), t.getContextClassLoader());
			// System.out.println("--------------------------------------------------");
		}
	}

}

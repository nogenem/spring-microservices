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
		// Fix: "Illegal access: this web application instance has been stopped
		// already."

		Set<Thread> threads = Thread.getAllStackTraces().keySet();
		for (Thread t : threads) {
			ClassLoader contextClassLoader = t.getContextClassLoader();
			Class<? extends ClassLoader> contextClassLoaderClass = contextClassLoader != null ? contextClassLoader.getClass()
					: null;
			if (contextClassLoader != null && contextClassLoaderClass != null
					&& contextClassLoaderClass.toString().contains("TomcatEmbeddedWebappClassLoader")) {
				// The parent is the correct loader:
				// "org.springframework.boot.devtools.restart.classloader.RestartClassLoader"
				contextClassLoader = contextClassLoader.getParent();
				t.setContextClassLoader(contextClassLoader);
			}

			// System.out.println("--------------------------------------------------");
			// System.out.printf("%s | %s | %d | %s | %s | %s\n", t.getName(),
			// t.getState(), t.getPriority(), t.isDaemon(),
			// contextClassLoaderClass,
			// contextClassLoader);
			// System.out.println("--------------------------------------------------");
		}
	}

}

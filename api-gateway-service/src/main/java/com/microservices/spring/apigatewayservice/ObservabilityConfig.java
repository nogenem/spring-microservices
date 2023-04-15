package com.microservices.spring.apigatewayservice;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.cloud.gateway.filter.headers.observation.GatewayContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;
import org.springframework.util.AntPathMatcher;

import io.micrometer.observation.ObservationRegistry;

@Configuration
public class ObservabilityConfig {

  @Bean
  ObservationRegistryCustomizer<ObservationRegistry> forceOnlyApiEndpointsForObservation() {
    AntPathMatcher pathMatcher = new AntPathMatcher("/");
    return (registry) -> registry.observationConfig().observationPredicate((name, context) -> {
      String path = "";

      if (context instanceof ServerRequestObservationContext observationContext) {
        path = observationContext.getCarrier().getPath().toString();
      } else if (context instanceof GatewayContext observationContext) {
        path = observationContext.getRequest().getPath().toString();
      }

      return !path.isEmpty() && pathMatcher.match("/api/**", path);
    });
  }

}

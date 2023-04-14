package com.microservices.spring.apigatewayservice;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.microservices.spring.apigatewayservice.exceptions.InvalidOrExpiredTokenException;
import com.microservices.spring.common.CustomHeaders;
import com.microservices.spring.common.JwtTokenService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

  private static final List<String> PUBLIC_ENDPOINTS = List.of(
      // Auth
      "/api/auth/signin",
      "/api/auth/signup",
      // Actuator
      "/actuator/**",
      // Eureka
      "/",
      "/eureka/**");
  private static final String TOKEN_HEADER_KEY = "Authorization";
  private static final String TOKEN_HEADER_KEY_PREFIX = "Bearer ";

  private final JwtTokenService jwtTokenService;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // https://github.com/roytuts/spring-cloud/blob/master/spring-cloud-gateway-security-jwt/spring-boot-cloud-gateway/src/main/java/com/roytuts/spring/boot/cloud/gateway/filter/JwtAuthenticationFilter.java
    ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

    Predicate<ServerHttpRequest> isSecuredEndpoint = r -> PUBLIC_ENDPOINTS.stream()
        .noneMatch(uri -> {
          if (uri.endsWith("/**")) {
            return r.getURI().getPath().startsWith(uri.substring(0, uri.length() - 3));
          } else if (uri.startsWith("**/")) {
            return r.getURI().getPath().endsWith(uri.substring(3, uri.length()));
          }
          return r.getURI().getPath().equals(uri);
        });

    if (isSecuredEndpoint.test(request)) {
      if (!request.getHeaders().containsKey(TOKEN_HEADER_KEY)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }

      String token = request.getHeaders().getOrEmpty(TOKEN_HEADER_KEY).get(0);

      if (token == null || token.isEmpty() || !token.startsWith(TOKEN_HEADER_KEY_PREFIX)) {
        throw new InvalidOrExpiredTokenException();
      }

      token = token.substring(TOKEN_HEADER_KEY_PREFIX.length(), token.length());
      Optional<String> userId = jwtTokenService.getUserIdFromToken(token);

      if (!userId.isPresent()) {
        throw new InvalidOrExpiredTokenException();
      }

      exchange.getRequest().mutate().header(CustomHeaders.USER_ID, userId.get()).build();
    }

    return chain.filter(exchange);
  }

}

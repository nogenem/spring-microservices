package com.microservices.spring.apigatewayservice.configs;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

  @Value("${api-gateway-service.admin.username}")
  private String adminUsername;

  @Value("${api-gateway-service.admin.password}")
  private String adminPassword;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.csrf(csrf -> csrf.disable());
    http.authorizeExchange(ex -> ex.pathMatchers("/admin/**").authenticated())
        .httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(withDefaults())
        .authorizeExchange(ex -> ex.anyExchange().permitAll());
    return http.build();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {
    UserDetails user = User
        .withUsername(adminUsername)
        .password(passwordEncoder().encode(adminPassword))
        .roles("ADMIN")
        .build();
    return new MapReactiveUserDetailsService(user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}

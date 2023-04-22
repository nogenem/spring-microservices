package com.microservices.spring.apigatewayservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

@Configuration
public class FixForGrafanaWebsocketConfig {

  // https://github.com/spring-cloud/spring-cloud-gateway/issues/145#issuecomment-1219851487

  @Bean
  @Primary
  public RequestUpgradeStrategy requestUpgradeStrategy() {
    return new TomcatRequestUpgradeStrategy();
  }

  @Bean
  @Primary
  WebSocketClient tomcatWebSocketClient() {
    return new TomcatWebSocketClient();
  }

}

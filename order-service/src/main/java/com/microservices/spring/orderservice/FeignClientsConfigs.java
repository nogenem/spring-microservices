package com.microservices.spring.orderservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

@Configuration
public class FeignClientsConfigs {

  @Bean
  public Logger.Level loggerLevel() {
    return Logger.Level.FULL;
  }

}

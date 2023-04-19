package com.microservices.spring.notificationservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.javafaker.Faker;

import feign.Client;

@Configuration
public class TestConfigs {

  @Bean
  public Faker faker() {
    return new Faker();
  }

  @Bean
  public Client feignClient() {
    // Feign' load balancer wasn't finding `localhost`
    // https://stackoverflow.com/a/51511614
    return new Client.Default(null, null);
  }

}

package com.microservices.spring.orderservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.javafaker.Faker;

@Configuration
public class TestConfigs {

  @Bean
  public Faker faker() {
    return new Faker();
  }

}

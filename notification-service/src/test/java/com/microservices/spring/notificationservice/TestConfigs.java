package com.microservices.spring.notificationservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.github.javafaker.Faker;
import com.microservices.spring.common.kafka.KafkaTopics;

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

  @Bean
  public NewTopic orderTopic() {
    return TopicBuilder.name(KafkaTopics.ORDER_TOPIC)
        .partitions(1)
        .replicas(1)
        .build();
  }

}

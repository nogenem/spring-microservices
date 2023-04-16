package com.microservices.spring.orderservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import com.microservices.spring.common.kafka.IKafkaEvent;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ObservationConfigs {

  private final KafkaTemplate<String, IKafkaEvent> kafkaTemplate;

  @Bean
  public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }

  @PostConstruct
  void setup() {
    kafkaTemplate.setObservationEnabled(true);
  }

}

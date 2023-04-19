package com.microservices.spring.notificationservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import com.microservices.spring.common.kafka.IKafkaEvent;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class ObservationConfigs {

  private final ConcurrentKafkaListenerContainerFactory<String, IKafkaEvent> concurrentKafkaListenerContainerFactory;

  @Bean
  public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }

  @PostConstruct
  void setup() {
    concurrentKafkaListenerContainerFactory.getContainerProperties().setObservationEnabled(true);
  }

}

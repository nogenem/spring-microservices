package com.microservices.spring.orderservice.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.microservices.spring.common.kafka.IKafkaEvent;

import io.micrometer.observation.annotation.Observed;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Observed(name = "kafka-producer-service")
public class KafkaProducerService {

  private final KafkaTemplate<String, IKafkaEvent> kafkaTemplate;

  public void sendEventOnTopic(String topic, IKafkaEvent event) {
    kafkaTemplate.send(topic, event);
  }

}

package com.microservices.spring.notificationservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.microservices.spring.common.kafka.KafkaTopics;
import com.microservices.spring.orderservicecontracts.events.OrderPlacedEvent;

import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Observed(name = "kafka-consumer-service")
public class KafkaConsumerService {

  @KafkaListener(topics = KafkaTopics.NOTIFICATION_TOPIC)
  public void handleOrderPlacedEvent(OrderPlacedEvent event) {
    log.info("[{}] Received event: {}", KafkaTopics.NOTIFICATION_TOPIC, event);
  }

}

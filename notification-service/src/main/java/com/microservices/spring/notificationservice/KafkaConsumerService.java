package com.microservices.spring.notificationservice;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.microservices.spring.common.kafka.KafkaTopics;
import com.microservices.spring.orderservicecontracts.events.OrderPlacedEvent;

import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Observed(name = "kafka-consumer-service")
@KafkaListener(topics = KafkaTopics.ORDER_TOPIC, groupId = "notification-service-order-topic")
public class KafkaConsumerService {

  @KafkaHandler
  public void handleOrderPlacedEvent(OrderPlacedEvent event) {
    log.info("[{}] Received event: {}", KafkaTopics.ORDER_TOPIC, event);
  }

  @KafkaHandler(isDefault = true)
  void handleUnknownMessages(@Payload Object unknown,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    log.warn("Server received unknown message {}, {}, {}, {}", offset, partitionId, topic, unknown);
  }

}

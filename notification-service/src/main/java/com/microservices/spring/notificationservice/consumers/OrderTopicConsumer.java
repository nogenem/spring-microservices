package com.microservices.spring.notificationservice.consumers;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.microservices.spring.authservicecontracts.responses.UserEmailResponse;
import com.microservices.spring.common.kafka.KafkaTopics;
import com.microservices.spring.notificationservice.MailService;
import com.microservices.spring.notificationservice.clients.AuthServiceClient;
import com.microservices.spring.orderservicecontracts.events.OrderPlacedEvent;

import io.micrometer.observation.annotation.Observed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
@Observed(name = "kafka-consumer-service")
@KafkaListener(topics = KafkaTopics.ORDER_TOPIC, groupId = "notification-service-order-topic")
public class OrderTopicConsumer {

  private final MailService mailService;
  private final AuthServiceClient authServiceClient;

  @KafkaHandler
  public void handleOrderPlacedEvent(OrderPlacedEvent event) {
    log.info("[{}] Received event: {}", KafkaTopics.ORDER_TOPIC, event);

    try {
      UserEmailResponse response = authServiceClient.findUserEmailByUserId(event.getUserId());
      mailService.sendOrderPlacedEmail(response.getEmail(), event.getOrderNumber());
    } catch (Exception e) {
      log.error("Exception caught while trying to send an email notification", e);
    }
  }

  @KafkaHandler(isDefault = true)
  void handleUnknownMessages(@Payload Object unknown,
      @Header(KafkaHeaders.OFFSET) long offset,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    // log.warn("Server received unknown message {}, {}, {}, {}", offset,
    // partitionId, topic, unknown);
  }

}

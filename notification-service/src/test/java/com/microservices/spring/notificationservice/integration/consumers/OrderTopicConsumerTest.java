package com.microservices.spring.notificationservice.integration.consumers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.microservices.spring.authservicecontracts.responses.UserEmailResponse;
import com.microservices.spring.common.kafka.IKafkaEvent;
import com.microservices.spring.common.kafka.KafkaTopics;
import com.microservices.spring.notificationservice.factories.events.FakeOrderPlacedEventFactory;
import com.microservices.spring.notificationservice.factories.responses.FakeUserEmailResponseFactory;
import com.microservices.spring.notificationservice.integration.BaseIntegrationTest;
import com.microservices.spring.orderservicecontracts.events.OrderPlacedEvent;

import jakarta.mail.internet.MimeMessage;

public class OrderTopicConsumerTest extends BaseIntegrationTest {

  @Autowired
  private FakeUserEmailResponseFactory fakeUserEmailResponseFactory;

  @Autowired
  private FakeOrderPlacedEventFactory fakeOrderPlacedEventFactory;

  @BeforeEach
  public void beforeEach() {
    setupKafkaProducer();
  }

  @Test
  @DisplayName("Should be able to consume OrderPlacedEvents")
  public void shouldBeAbleToConsumeOrderPlacedEvents() throws JsonProcessingException {
    OrderPlacedEvent event = fakeOrderPlacedEventFactory.createOne();

    UserEmailResponse userEmailResponse = fakeUserEmailResponseFactory.createOne();
    userEmailResponse.setId(event.getUserId());

    stubUserEmailCall(userEmailResponse);

    ProducerRecord<String, IKafkaEvent> record = new ProducerRecord<String, IKafkaEvent>(KafkaTopics.ORDER_TOPIC,
        event);
    kafkaProducer.send(record);

    await().atMost(3, SECONDS).untilAsserted(() -> {
      MimeMessage[] emails = greenMail.getReceivedMessagesForDomain(userEmailResponse.getEmail());
      Assertions.assertEquals(1, emails.length);

      MimeMessage email = emails[0];
      Assertions.assertTrue(GreenMailUtil.getBody(email).contains(event.getOrderNumber().toString()));
      Assertions.assertEquals(1, email.getAllRecipients().length);
      Assertions.assertEquals(userEmailResponse.getEmail(), email.getAllRecipients()[0].toString());
    });
  }

  private void stubUserEmailCall(UserEmailResponse response) throws JsonProcessingException {
    stubFor(WireMock.get(urlPathMatching("/api/auth/users/([^/]*)/email"))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(response))));
  }

}

package com.microservices.spring.orderservice.integration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.spring.common.kafka.IKafkaEvent;
import com.microservices.spring.common.kafka.KafkaTopics;
import com.microservices.spring.orderservice.OrderRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
public abstract class BaseIntegrationTest {

  public static PostgreSQLContainer<?> postgresDBContainer = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:15.2"));
  public static KafkaContainer kafkaContainer = new KafkaContainer(
      DockerImageName.parse("confluentinc/cp-kafka:7.3.2"));

  @Autowired
  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected OrderRepository orderRepository;

  protected KafkaConsumer<String, IKafkaEvent> kafkaConsumer = null;

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    Startables.deepStart(Stream.of(postgresDBContainer, kafkaContainer)).join();

    registry.add("spring.datasource.driver-class-name", postgresDBContainer::getDriverClassName);
    registry.add("spring.datasource.url", postgresDBContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresDBContainer::getUsername);
    registry.add("spring.datasource.password", postgresDBContainer::getPassword);

    registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
  }

  @AfterEach
  public void cleanUpDatabase() throws SQLException {
    orderRepository.deleteAll();
  }

  protected void setupKafkaConsumer() {
    if (kafkaConsumer != null) {
      kafkaConsumer.close();
    }

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES,
        "com.microservices.spring.orderservicecontracts.events");

    kafkaConsumer = new KafkaConsumer<>(props);
    kafkaConsumer.subscribe(List.of(KafkaTopics.ORDER_TOPIC));
  }

}

package com.microservices.spring.apigatewayservice;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
public abstract class BaseIntegrationTest {

  @Autowired
  protected ApplicationContext context;

  @Autowired
  protected ObjectMapper objectMapper;

  protected WebTestClient client;

  @BeforeEach
  public void beforeEach() {
    client = WebTestClient.bindToApplicationContext(context)
        .build();
  }

  protected void printResponseBody(ResponseSpec spec) {
    spec.expectBody()
        .consumeWith(System.out::println);
  }

}

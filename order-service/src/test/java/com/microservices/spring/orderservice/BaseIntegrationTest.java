package com.microservices.spring.orderservice;

import java.sql.SQLException;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

  @ClassRule
  static PostgreSQLContainer<?> postgresDBContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.2"));

  @Autowired
  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected OrderRepository orderRepository;

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    postgresDBContainer.start();

    registry.add("spring.datasource.driver-class-name", postgresDBContainer::getDriverClassName);
    registry.add("spring.datasource.url", postgresDBContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresDBContainer::getUsername);
    registry.add("spring.datasource.password", postgresDBContainer::getPassword);
  }

  @AfterEach
  public void cleanUpDatabase() throws SQLException {
    orderRepository.deleteAll();
  }

}

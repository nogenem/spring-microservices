package com.microservices.spring.orderservice.integration.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.orderservice.BaseIntegrationTest;
import com.microservices.spring.orderservice.OrderRepository;
import com.microservices.spring.orderservice.exceptions.InvalidOrderNumberException;
import com.microservices.spring.orderservice.exceptions.OrderWithThisOrderNumberNotFoundException;
import com.microservices.spring.orderservice.factories.FakeOrderFactory;
import com.microservices.spring.orderservice.models.Order;

public class FindOrderByOrderNumberTest extends BaseIntegrationTest {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private FakeOrderFactory orderFactory;

  @Test
  @DisplayName("Should be able to get orders by order_number")
  public void shouldBeAbleToGetOrdersByOrderNumber() throws JsonProcessingException, Exception {
    Order savedOrder = orderFactory.createOne(2);
    orderRepository.save(savedOrder);

    String orderNumber = savedOrder.getOrderNumber().toString();
    ResultActions resultActions = mvc.perform(get("/api/orders/{order_number}", orderNumber)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
        .andExpect(jsonPath("$.orderNumber").value(orderNumber));
  }

  @Test
  @DisplayName("Should not be able to get orders with an invalid order_number")
  public void shouldNotBeAbleToGetOrdersWithAnInvalidOrderNumber() throws JsonProcessingException, Exception {
    Order savedOrder = orderFactory.createOne(2);
    orderRepository.save(savedOrder);

    // Invalid UUID
    String orderNumber = savedOrder.getOrderNumber() + "_invalid";
    ResultActions resultActions = mvc.perform(get("/api/orders/{order_number}", orderNumber)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(InvalidOrderNumberException.CODE));

    // OrderNumber not found
    orderNumber = UUID.randomUUID().toString();
    resultActions = mvc.perform(get("/api/orders/{order_number}", orderNumber)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(OrderWithThisOrderNumberNotFoundException.CODE));
  }

}

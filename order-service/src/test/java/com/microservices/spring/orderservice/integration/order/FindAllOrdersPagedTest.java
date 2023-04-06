package com.microservices.spring.orderservice.integration.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.orderservice.BaseIntegrationTest;
import com.microservices.spring.orderservice.OrderRepository;
import com.microservices.spring.orderservice.factories.FakeOrderFactory;
import com.microservices.spring.orderservice.models.Order;

public class FindAllOrdersPagedTest extends BaseIntegrationTest {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private FakeOrderFactory orderFactory;

  @Test
  @DisplayName("Should be able to get saved orders")
  public void shouldBeAbleToGetSavedOrders() throws JsonProcessingException, Exception {
    List<Order> savedOrders = orderFactory.createMany(2, 2);
    orderRepository.saveAll(savedOrders);

    ResultActions resultActions = mvc.perform(get("/api/orders")
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(savedOrders.size()));
  }

  @Test
  @DisplayName("Should be able to control the page index and size of the returned orders")
  public void shouldBeAbleToControlThePageIndexAndSizeOfTheReturnedOrders()
      throws JsonProcessingException, Exception {
    List<Order> savedOrders = orderFactory.createMany(7, 2);
    orderRepository.saveAll(savedOrders);

    // First page
    int page = 0;
    int size = 5;
    ResultActions resultActions = mvc.perform(get("/api/orders")
        .param("page", String.valueOf(page))
        .param("size", String.valueOf(size))
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(size))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(false))
        .andExpect(jsonPath("$.page").value(page));

    // Second page
    page = 1;
    resultActions = mvc.perform(get("/api/orders")
        .param("page", String.valueOf(page))
        .param("size", String.valueOf(size))
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(savedOrders.size() - size))
        .andExpect(jsonPath("$.first").value(false))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.page").value(page));
  }

  @Test
  @DisplayName("Should be able to control the sorting of the returned orders")
  public void shouldBeAbleToControlTheSortingOfTheReturnedOrders() throws JsonProcessingException, Exception {
    Order firstSavedOrder = orderFactory.createOne(2);
    orderRepository.save(firstSavedOrder);

    Order lastSavedOrder = orderFactory.createOne(2);
    orderRepository.save(lastSavedOrder);

    // ASC order
    ResultActions resultActions = mvc.perform(get("/api/orders")
        .param("sort", "createdAt")
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].orderNumber").value(firstSavedOrder.getOrderNumber().toString()));

    // DESC order
    resultActions = mvc.perform(get("/api/orders")
        .param("sort", "-createdAt")
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].orderNumber").value(lastSavedOrder.getOrderNumber().toString()));
  }

}

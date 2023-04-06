package com.microservices.spring.orderservice.integration.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.common.exceptions.ValidationErrorsException;
import com.microservices.spring.orderservice.BaseIntegrationTest;
import com.microservices.spring.orderservice.factories.requests.FakePlaceOrderRequestFactory;
import com.microservices.spring.orderservice.requests.PlaceOrderRequest;

public class PlaceOrderTest extends BaseIntegrationTest {

  @Autowired
  private FakePlaceOrderRequestFactory placeOrderRequestFactory;

  @Test
  @DisplayName("Should be able to place orders")
  public void shouldBeAbleToPlaceOrders() throws JsonProcessingException, Exception {
    PlaceOrderRequest request = placeOrderRequestFactory.createOne(2);

    ResultActions resultActions = mvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.lineItems.length()").value(request.getLineItems().size()))
        .andExpect(jsonPath("$.orderNumber").isNotEmpty())
        .andExpect(jsonPath("$.id").isNotEmpty());

    Assertions.assertEquals(1, orderRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to place order that dont comply by the validations")
  public void shouldNotBeAbleToPlaceOrderThatDontComplyByTheValidations()
      throws JsonProcessingException, Exception {
    // Zero line items
    PlaceOrderRequest request = PlaceOrderRequest.builder()
        .lineItems(new ArrayList<>())
        .build();

    ResultActions resultActions = mvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.lineItems").isNotEmpty());

    Assertions.assertEquals(0, orderRepository.findAll().size());

    // Line item with invalid quantity
    request = placeOrderRequestFactory.createOne(1);
    request.getLineItems().get(0).setQuantity(-1);

    resultActions = mvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors['lineItems[0].quantity']").isNotEmpty());

    Assertions.assertEquals(0, orderRepository.findAll().size());
  }

}

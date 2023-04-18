package com.microservices.spring.orderservice.integration.order;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.microservices.spring.common.CustomHeaders;
import com.microservices.spring.common.exceptions.InvalidUserIdException;
import com.microservices.spring.common.exceptions.ValidationErrorsException;
import com.microservices.spring.common.kafka.KafkaTopics;
import com.microservices.spring.inventoryservicecontracts.responses.InventoryQuantityResponse;
import com.microservices.spring.orderservice.exceptions.OneOrMoreProductsOutOfStockException;
import com.microservices.spring.orderservice.factories.requests.FakePlaceOrderRequestFactory;
import com.microservices.spring.orderservice.factories.responses.FakeProductPriceResponseFactory;
import com.microservices.spring.orderservice.integration.BaseIntegrationTest;
import com.microservices.spring.orderservicecontracts.events.OrderPlacedEvent;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderLineItemRequest;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderRequest;
import com.microservices.spring.productservicecontracts.responses.ProductPriceResponse;

public class PlaceOrderTest extends BaseIntegrationTest {

  @Autowired
  private FakePlaceOrderRequestFactory placeOrderRequestFactory;

  @Autowired
  private FakeProductPriceResponseFactory productPriceResponseFactory;

  @BeforeEach
  public void beforeEach() {
    setupOrderPlacedConsumer();
  }

  @Test
  @DisplayName("Should be able to place orders")
  public void shouldBeAbleToPlaceOrders() throws JsonProcessingException, Exception {
    PlaceOrderRequest request = placeOrderRequestFactory.createOne(2);
    String userId = UUID.randomUUID().toString();

    stubInventoriesQuantitiesCall(request.getLineItems(), true);
    stubProductsPricesCall(request.getLineItems());

    ResultActions resultActions = mvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .header(CustomHeaders.USER_ID, userId)
        .content(objectMapper.writeValueAsString(request)));

    var singleRecord = KafkaTestUtils.getSingleRecord(orderPlacedConsumer,
        KafkaTopics.NOTIFICATION_TOPIC, Duration.ofSeconds(5));
    OrderPlacedEvent event = singleRecord.value();

    Assertions.assertNotNull(event);

    resultActions
        // .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.lineItems.length()").value(request.getLineItems().size()))
        .andExpect(jsonPath("$.orderNumber").value(event.getOrderNumber().toString()))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.id").isNotEmpty());

    Assertions.assertEquals(1, orderRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to place orders with products that are out of stock")
  public void shouldNotBeAbleToPlaceOrdersWithProductsThatAreOutOfStock() throws JsonProcessingException, Exception {
    PlaceOrderRequest request = placeOrderRequestFactory.createOne(2);
    String userId = UUID.randomUUID().toString();

    stubInventoriesQuantitiesCall(request.getLineItems(), false);
    stubProductsPricesCall(request.getLineItems());

    ResultActions resultActions = mvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .header(CustomHeaders.USER_ID, userId)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(OneOrMoreProductsOutOfStockException.CODE));

    Assertions.assertEquals(0, orderRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to place orders with an invalid uuid userId")
  public void shouldNotBeAbleToPlaceOrdersWithAnInvalidUuidUserId() throws JsonProcessingException, Exception {
    PlaceOrderRequest request = placeOrderRequestFactory.createOne(2);
    String userId = UUID.randomUUID().toString() + "_invalid";

    ResultActions resultActions = mvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .header(CustomHeaders.USER_ID, userId)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(InvalidUserIdException.CODE));

    Assertions.assertEquals(0, orderRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to place order that dont comply by the validations")
  public void shouldNotBeAbleToPlaceOrderThatDontComplyByTheValidations()
      throws JsonProcessingException, Exception {
    // Zero line items
    PlaceOrderRequest request = PlaceOrderRequest.builder()
        .lineItems(new ArrayList<>())
        .build();
    String userId = UUID.randomUUID().toString();

    ResultActions resultActions = mvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .header(CustomHeaders.USER_ID, userId)
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

  private void stubInventoriesQuantitiesCall(List<PlaceOrderLineItemRequest> lineItems, boolean shouldAllBeInStock)
      throws JsonProcessingException {
    List<InventoryQuantityResponse> response = lineItems.stream()
        .map(lineItem -> {
          int quantity = shouldAllBeInStock ? lineItem.getQuantity() : 0;
          return new InventoryQuantityResponse(lineItem.getProductSku(), quantity);
        }).toList();

    stubFor(WireMock.get(urlPathMatching("/api/inventories/([^/]*)/quantities"))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(response))));
  }

  private void stubProductsPricesCall(List<PlaceOrderLineItemRequest> lineItems)
      throws JsonProcessingException {
    List<ProductPriceResponse> response = lineItems.stream()
        .map(lineItem -> {
          ProductPriceResponse response2 = productPriceResponseFactory.createOne();
          response2.setSku(lineItem.getProductSku());
          return response2;
        }).toList();

    stubFor(WireMock.get(urlPathMatching("/api/products/([^/]*)/prices"))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(response))));
  }

}

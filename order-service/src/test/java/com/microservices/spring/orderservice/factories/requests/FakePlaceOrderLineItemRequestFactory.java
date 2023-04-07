package com.microservices.spring.orderservice.factories.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderLineItemRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakePlaceOrderLineItemRequestFactory {

  private final Faker faker;

  public PlaceOrderLineItemRequest createOne() {
    return PlaceOrderLineItemRequest.builder()
        .productSku(UUID.randomUUID().toString())
        .quantity(faker.random().nextInt(1, 10))
        .build();
  }

  public List<PlaceOrderLineItemRequest> createMany(int length) {
    List<PlaceOrderLineItemRequest> lineItems = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      lineItems.add(createOne());
    }

    return lineItems;
  }

}

package com.microservices.spring.orderservice.factories.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.orderservice.requests.StoreOrderLineItemRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeStoreOrderLineItemRequestFactory {

  private final Faker faker;

  public StoreOrderLineItemRequest createOne() {
    return StoreOrderLineItemRequest.builder()
        .productSku(UUID.randomUUID().toString())
        .quantity(faker.random().nextInt(1, 10))
        .build();
  }

  public List<StoreOrderLineItemRequest> createMany(int length) {
    List<StoreOrderLineItemRequest> lineItems = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      lineItems.add(createOne());
    }

    return lineItems;
  }

}

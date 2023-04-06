package com.microservices.spring.orderservice.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.orderservice.models.OrderLineItem;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeOrderLineItemFactory {

  private final Faker faker;

  public OrderLineItem createOne() {
    return OrderLineItem.builder()
        .productSku(UUID.randomUUID().toString())
        .productPrice(faker.random().nextInt(1000, 2000))
        .quantity(faker.random().nextInt(1, 10))
        .build();
  }

  public List<OrderLineItem> createMany(int length) {
    List<OrderLineItem> lineItems = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      lineItems.add(createOne());
    }

    return lineItems;
  }

}

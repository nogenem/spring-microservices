package com.microservices.spring.inventoryservice.factories.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeStoreInventoryRequestFactory {

  private final Faker faker;

  public StoreInventoryRequest createOne() {
    String sku = UUID.randomUUID().toString();
    return StoreInventoryRequest.builder()
        .sku(sku)
        .quantity(faker.random().nextInt(100))
        .build();
  }

  public List<StoreInventoryRequest> createMany(int length) {
    List<StoreInventoryRequest> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

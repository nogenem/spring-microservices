package com.microservices.spring.inventoryservice.factories.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.inventoryservicecontracts.requests.UpdateInventoryRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeUpdateInventoryRequestFactory {

  private final Faker faker;

  public UpdateInventoryRequest createOne() {
    return UpdateInventoryRequest.builder()
        .quantity(faker.random().nextInt(1, 100))
        .build();
  }

  public List<UpdateInventoryRequest> createMany(int length) {
    List<UpdateInventoryRequest> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

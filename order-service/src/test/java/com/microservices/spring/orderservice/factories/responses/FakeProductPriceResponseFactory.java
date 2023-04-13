package com.microservices.spring.orderservice.factories.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.productservicecontracts.responses.ProductPriceResponse;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeProductPriceResponseFactory {

  private final Faker faker;

  public ProductPriceResponse createOne() {
    return ProductPriceResponse.builder()
        .sku(UUID.randomUUID().toString())
        .price(faker.random().nextInt(1000, 2000))
        .build();
  }

  public List<ProductPriceResponse> createMany(int length) {
    List<ProductPriceResponse> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

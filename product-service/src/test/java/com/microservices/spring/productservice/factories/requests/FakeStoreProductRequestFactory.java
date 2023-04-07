package com.microservices.spring.productservice.factories.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import com.microservices.spring.productservicecontracts.requests.StoreProductRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeStoreProductRequestFactory {

  private final Faker faker;
  private final Slugify slg = Slugify.builder().build();

  public StoreProductRequest createOne() {
    String name = faker.name().fullName();
    String slug = slg.slugify(name);
    return StoreProductRequest.builder()
        .sku(slug)
        .slug(slug)
        .name(name)
        .description(faker.lorem().sentence(1))
        .price(faker.random().nextLong(2500))
        .build();
  }

  public List<StoreProductRequest> createMany(int length) {
    List<StoreProductRequest> products = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      products.add(createOne());
    }

    return products;
  }

}

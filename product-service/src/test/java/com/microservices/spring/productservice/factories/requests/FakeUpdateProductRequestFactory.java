package com.microservices.spring.productservice.factories.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import com.microservices.spring.productservicecontracts.requests.UpdateProductRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeUpdateProductRequestFactory {

  private final Faker faker;
  private final Slugify slg = Slugify.builder().build();

  public UpdateProductRequest createOne() {
    String name = faker.name().fullName();
    String slug = slg.slugify(name);
    return UpdateProductRequest.builder()
        .slug(slug)
        .name(name)
        .description(faker.lorem().sentence(1))
        .price(faker.random().nextInt(1000, 2000))
        .build();
  }

  public List<UpdateProductRequest> createMany(int length) {
    List<UpdateProductRequest> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

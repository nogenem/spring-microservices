package com.microservices.spring.productservice.factories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import com.microservices.spring.productservice.Product;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeProductFactory {

  private final Faker faker;
  private final Slugify slg = Slugify.builder().build();

  public Product createOne() {
    String name = faker.name().fullName();
    String slug = slg.slugify(name);
    return Product.builder()
        .sku(slug)
        .slug(slug)
        .name(name)
        .description(faker.lorem().paragraph(1))
        .price(faker.random().nextInt(1000, 2000))
        .build();
  }

  public List<Product> createMany(int length) {
    List<Product> products = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      products.add(createOne());
    }

    return products;
  }

}
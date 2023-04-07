package com.microservices.spring.inventoryservice.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.inventoryservice.Inventory;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeInventoryFactory {

  private final Faker faker;

  public Inventory createOne() {
    String sku = UUID.randomUUID().toString();
    return Inventory.builder()
        .sku(sku)
        .quantity(faker.random().nextInt(1, 100))
        .build();
  }

  public List<Inventory> createMany(int length) {
    List<Inventory> inventories = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      inventories.add(createOne());
    }

    return inventories;
  }

}

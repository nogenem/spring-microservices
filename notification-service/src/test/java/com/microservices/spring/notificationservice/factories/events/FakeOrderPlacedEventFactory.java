package com.microservices.spring.notificationservice.factories.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microservices.spring.orderservicecontracts.events.OrderPlacedEvent;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeOrderPlacedEventFactory {

  public OrderPlacedEvent createOne() {
    return OrderPlacedEvent.builder()
        .orderNumber(UUID.randomUUID())
        .userId(UUID.randomUUID())
        .build();
  }

  public List<OrderPlacedEvent> createMany(int length) {
    List<OrderPlacedEvent> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

package com.microservices.spring.orderservice.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservice.models.OrderLineItem;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeOrderFactory {

  private final FakeOrderLineItemFactory fakeOrderLineItemFactory;

  public Order createOne(int nLineItems) {
    List<OrderLineItem> lineItems = fakeOrderLineItemFactory.createMany(nLineItems);

    Order order = Order.builder()
        .orderNumber(UUID.randomUUID())
        .build();
    order.setLineItems(lineItems);

    return order;
  }

  public List<Order> createMany(int nOrders, int nLineItems) {
    List<Order> orders = new ArrayList<>();

    for (int i = 0; i < nOrders; i++) {
      orders.add(createOne(nLineItems));
    }

    return orders;
  }

}

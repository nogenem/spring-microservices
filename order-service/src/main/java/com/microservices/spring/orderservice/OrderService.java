package com.microservices.spring.orderservice;

import org.springframework.stereotype.Service;

import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservice.requests.StoreOrderRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

  private OrderRepository orderRepository;
  private final MapStructMapper mapStructMapper;

  public Order placeOrder(StoreOrderRequest request) {
    // TODO: Check inventory-service for the availability of the products
    // TODO: Get product prices from the product-service

    Order order = mapStructMapper.storeOrderRequestToOrder(request);
    order.getLineItems().stream().forEach(item -> {
      item.setOrder(order);
      item.setProductPrice(1000); // TODO: Change this
    });

    orderRepository.save(order);

    log.info("Order saved. Id: {} - OrderNumber: {}", order.getId(), order.getOrderNumber());

    return order;
  }

}

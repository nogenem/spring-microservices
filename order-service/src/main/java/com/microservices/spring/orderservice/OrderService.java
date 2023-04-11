package com.microservices.spring.orderservice;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.spring.orderservice.exceptions.OrderWithThisOrderNumberNotFoundException;
import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

  private OrderRepository orderRepository;
  private final MapStructMapper mapStructMapper;

  public Order placeOrder(PlaceOrderRequest request, UUID userId) {
    // TODO: Check inventory-service for the availability of the products
    // TODO: Get product prices from the product-service

    Order order = mapStructMapper.placeOrderRequestToOrder(request);
    order.setUserId(userId);
    order.getLineItems().stream().forEach(item -> {
      item.setOrder(order);
      item.setProductPrice(1000); // TODO: Change this
    });

    orderRepository.save(order);

    log.info("Order saved. Id: {} - OrderNumber: {}", order.getId(), order.getOrderNumber());

    return order;
  }

  public Page<Order> findAll(Pageable pageOptions) {
    return orderRepository.findAll(pageOptions);
  }

  public Order findByOrderNumber(UUID orderNumber) {
    return orderRepository.findByOrderNumber(orderNumber)
        .orElseThrow(() -> new OrderWithThisOrderNumberNotFoundException(orderNumber));
  }

}

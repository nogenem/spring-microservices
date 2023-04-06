package com.microservices.spring.orderservice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservice.requests.StoreOrderRequest;
import com.microservices.spring.orderservice.responses.OrderResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/orders")
@Validated
public class OrderController {

  private final OrderService orderService;
  private final MapStructMapper mapStructMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse placeOrder(@Valid @RequestBody StoreOrderRequest request) {
    Order order = orderService.placeOrder(request);

    return mapStructMapper.orderToOrderResponse(order);
  }

}

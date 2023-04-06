package com.microservices.spring.orderservice;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservices.spring.orderservice.models.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  Optional<Order> findByOrderNumber(UUID orderNumber);

}

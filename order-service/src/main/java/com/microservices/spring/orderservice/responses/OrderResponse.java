package com.microservices.spring.orderservice.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderResponse {

  private UUID id;
  private UUID orderNumber;
  private List<OrderLineItemResponse> lineItems;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}

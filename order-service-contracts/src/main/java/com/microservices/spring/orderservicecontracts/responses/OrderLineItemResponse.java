package com.microservices.spring.orderservicecontracts.responses;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderLineItemResponse {

  private UUID id;
  private String productSku;
  private Integer productPrice;
  private Integer quantity;

}

package com.microservices.spring.inventoryservice.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InventoryResponse {

  private UUID id;
  private String sku;
  private Integer quantity;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}

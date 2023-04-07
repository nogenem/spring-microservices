package com.microservices.spring.inventoryservicecontracts.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import com.microservices.spring.common.responses.IEntityResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InventoryResponse implements IEntityResponse {

  private UUID id;
  private String sku;
  private Integer quantity;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}

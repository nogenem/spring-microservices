package com.microservices.spring.inventoryservicecontracts.responses;

import com.microservices.spring.common.responses.IEntityResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InventoryQuantityResponse implements IEntityResponse {

  private String sku;
  private Integer quantity;

}

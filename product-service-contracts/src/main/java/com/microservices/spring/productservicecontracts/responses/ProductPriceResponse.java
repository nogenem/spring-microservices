package com.microservices.spring.productservicecontracts.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductPriceResponse {

  private String sku;
  private Integer price;

}

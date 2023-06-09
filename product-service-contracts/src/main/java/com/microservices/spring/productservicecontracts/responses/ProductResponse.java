package com.microservices.spring.productservicecontracts.responses;

import java.time.LocalDateTime;

import com.microservices.spring.common.responses.IEntityResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductResponse implements IEntityResponse {

  private String id;
  private String sku;
  private String slug;
  private String name;
  private String description;
  private Integer price;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}

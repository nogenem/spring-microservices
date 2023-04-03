package com.microservices.spring.productservice.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductResponse {

  private String id;
  private String sku;
  private String slug;
  private String name;
  private String description;
  private Long price;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
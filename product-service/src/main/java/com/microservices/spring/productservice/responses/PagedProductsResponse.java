package com.microservices.spring.productservice.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PagedProductsResponse {

  private List<ProductResponse> content;
  private int totalElements;
  private int totalPages;
  private int page;
  private boolean first;
  private boolean last;

}

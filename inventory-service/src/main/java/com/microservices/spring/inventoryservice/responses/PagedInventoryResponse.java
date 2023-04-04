package com.microservices.spring.inventoryservice.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PagedInventoryResponse {

  private List<InventoryResponse> content;
  private int totalElements;
  private int totalPages;
  private int page;
  private boolean first;
  private boolean last;

}

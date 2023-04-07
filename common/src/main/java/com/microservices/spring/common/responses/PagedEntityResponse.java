package com.microservices.spring.common.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PagedEntityResponse<R extends IEntityResponse> {

  private List<R> content;
  private int totalElements;
  private int totalPages;
  private int page;
  private boolean first;
  private boolean last;

}

package com.microservices.spring.productservice.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

  @Size(min = 4, message = "length must be greater or equals to 4")
  private String slug;

  @NotNull
  @Size(min = 4, message = "length must be greater or equals to 4")
  private String name;

  private String description;

  @NotNull
  @Min(value = 1)
  private Long price;

}

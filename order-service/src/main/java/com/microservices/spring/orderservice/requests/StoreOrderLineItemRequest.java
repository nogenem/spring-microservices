package com.microservices.spring.orderservice.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StoreOrderLineItemRequest {

  @NotNull
  @Size(min = 4, max = 40, message = "length must be between 4 and 40")
  @Pattern(regexp = "[a-zA-Z0-9\\-_]+", message = "this field must contain only letters, numbers or the characteres: - and _")
  private String productSku;

  @NotNull
  @Min(value = 1)
  private Integer quantity;

}

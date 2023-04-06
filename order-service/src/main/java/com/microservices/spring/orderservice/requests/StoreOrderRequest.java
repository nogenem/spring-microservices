package com.microservices.spring.orderservice.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreOrderRequest {

  @Size(min = 1, message = "need at least one line item")
  @Valid
  List<StoreOrderLineItemRequest> lineItems;

}

package com.microservices.spring.orderservice.factories.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.microservices.spring.orderservice.requests.PlaceOrderRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakePlaceOrderRequestFactory {

  private final FakePlaceOrderLineItemRequestFactory fakePlaceOrderLineItemFactory;

  public PlaceOrderRequest createOne(int nLineItemRequests) {
    return PlaceOrderRequest.builder()
        .lineItems(fakePlaceOrderLineItemFactory.createMany(nLineItemRequests))
        .build();
  }

  public List<PlaceOrderRequest> createMany(int nOrderRequests, int nLineItemRequests) {
    List<PlaceOrderRequest> lineItems = new ArrayList<>();

    for (int i = 0; i < nOrderRequests; i++) {
      lineItems.add(createOne(nLineItemRequests));
    }

    return lineItems;
  }

}

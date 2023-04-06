package com.microservices.spring.orderservice.factories.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.microservices.spring.orderservice.requests.StoreOrderRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeStoreOrderRequestFactory {

  private final FakeStoreOrderLineItemRequestFactory fakeStoreOrderLineItemFactory;

  public StoreOrderRequest createOne(int nLineItemRequests) {
    return StoreOrderRequest.builder()
        .lineItems(fakeStoreOrderLineItemFactory.createMany(nLineItemRequests))
        .build();
  }

  public List<StoreOrderRequest> createMany(int nOrderRequests, int nLineItemRequests) {
    List<StoreOrderRequest> lineItems = new ArrayList<>();

    for (int i = 0; i < nOrderRequests; i++) {
      lineItems.add(createOne(nLineItemRequests));
    }

    return lineItems;
  }

}

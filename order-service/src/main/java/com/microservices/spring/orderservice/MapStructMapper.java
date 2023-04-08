package com.microservices.spring.orderservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.microservices.spring.common.responses.PagedEntityResponse;
import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservice.models.OrderLineItem;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderLineItemRequest;
import com.microservices.spring.orderservicecontracts.requests.PlaceOrderRequest;
import com.microservices.spring.orderservicecontracts.responses.OrderResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderNumber", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Order placeOrderRequestToOrder(PlaceOrderRequest orderRequest);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "productPrice", ignore = true)
  OrderLineItem placeOrderLineItemRequestToOrderLineItem(PlaceOrderLineItemRequest orderLineItemRequest);

  OrderResponse orderToOrderResponse(Order order);

  @Mapping(target = "page", source = "number")
  @Mapping(target = "content", source = "content", defaultExpression = "java(new java.util.ArrayList<>())")
  PagedEntityResponse<OrderResponse> pagedOrderToPagedOrderResponse(Page<Order> pagedOrder);

}

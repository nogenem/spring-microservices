package com.microservices.spring.orderservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservices.spring.common.exceptions.ApiException;
import com.microservices.spring.common.responses.ExceptionResponse;
import com.microservices.spring.orderservice.models.Order;
import com.microservices.spring.orderservice.models.OrderLineItem;
import com.microservices.spring.orderservice.requests.StoreOrderLineItemRequest;
import com.microservices.spring.orderservice.requests.StoreOrderRequest;
import com.microservices.spring.orderservice.responses.OrderResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderNumber", expression = "java(java.util.UUID.randomUUID())")
  Order storeOrderRequestToOrder(StoreOrderRequest orderRequest);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "productPrice", ignore = true)
  OrderLineItem storeOrderLineItemRequestToOrderLineItem(StoreOrderLineItemRequest orderLineItemRequest);

  OrderResponse orderToOrderResponse(Order order);

  @Mapping(target = "stackTrace", ignore = true)
  ExceptionResponse apiExceptionToExceptionResponse(ApiException apiException);

}

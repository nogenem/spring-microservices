package com.microservices.spring.orderservice.exceptions;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class OrderWithThisOrderNumberNotFoundException extends ApiException {

  public final static String CODE = "ORDER_WITH_THIS_ORDER_NUMBER_NOT_FOUND";

  public OrderWithThisOrderNumberNotFoundException(UUID orderNumber) {
    super(HttpStatus.NOT_FOUND.value(), CODE,
        String.format("An order with the orderNumber: %s wasn't found.", orderNumber.toString()));
  }

}

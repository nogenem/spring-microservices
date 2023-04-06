package com.microservices.spring.orderservice.exceptions;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class InvalidOrderNumberException extends ApiException {

  public final static String CODE = "INVALID_ORDER_NUMBER";

  public InvalidOrderNumberException(String orderNumber) {
    super(HttpStatus.BAD_REQUEST.value(), CODE,
        String.format("Invalid order_number: %s.", orderNumber));
  }

}

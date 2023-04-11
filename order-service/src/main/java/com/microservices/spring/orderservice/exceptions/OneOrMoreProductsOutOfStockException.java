package com.microservices.spring.orderservice.exceptions;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class OneOrMoreProductsOutOfStockException extends ApiException {

  public final static String CODE = "ONE_OR_MORE_PRODUCTS_OUT_OF_STOCK";

  public OneOrMoreProductsOutOfStockException() {
    super(HttpStatus.BAD_REQUEST.value(), CODE, "One or more products are out of stock.");
  }

}

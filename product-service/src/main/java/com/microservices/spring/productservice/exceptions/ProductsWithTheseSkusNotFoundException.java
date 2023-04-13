package com.microservices.spring.productservice.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class ProductsWithTheseSkusNotFoundException extends ApiException {

  public final static String CODE = "PRODUCTS_WITH_THESE_SKUS_NOT_FOUND";

  public ProductsWithTheseSkusNotFoundException(List<String> skus) {
    super(HttpStatus.NOT_FOUND.value(), CODE,
        String.format("Products with the skus: %s weren't found.", skus));
  }

}

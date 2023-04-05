package com.microservices.spring.productservice.exceptions;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class ProductWithThisSkuNotFoundException extends ApiException {

  public final static String CODE = "PRODUCT_WITH_THIS_SKU_NOT_FOUND";

  public ProductWithThisSkuNotFoundException(String sku) {
    super(HttpStatus.NOT_FOUND.value(), CODE,
        String.format("A product with the sku: %s wasn't found.", sku));
  }

}

package com.microservices.spring.productservice.exceptions;

public class ProductWithThisSkuOrSlugAlreadyExistsException extends RuntimeException {

  public ProductWithThisSkuOrSlugAlreadyExistsException(String sku, String slug) {
    super(String.format("A product with the sku: %s or the slug: %s already exists.", sku, slug));
  }

}

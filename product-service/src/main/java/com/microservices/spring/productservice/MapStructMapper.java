package com.microservices.spring.productservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.microservices.spring.productservice.exceptions.ApiException;
import com.microservices.spring.productservice.requests.StoreProductRequest;
import com.microservices.spring.productservice.responses.ExceptionResponse;
import com.microservices.spring.productservice.responses.PagedProductsResponse;
import com.microservices.spring.productservice.responses.ProductResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product storeProductRequestToProduct(StoreProductRequest storeProductRequest);

  ProductResponse productToProductResponse(Product product);

  @Mapping(target = "page", source = "number")
  @Mapping(target = "content", source = "content", defaultExpression = "java(new java.util.ArrayList<>())")
  PagedProductsResponse pagedProductsToPagedProductsResponse(Page<Product> pagedProducts);

  @Mapping(target = "stackTrace", ignore = true)
  ExceptionResponse apiExceptionToExceptionResponse(ApiException apiException);

}
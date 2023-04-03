package com.microservices.spring.productservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservices.spring.productservice.requests.StoreProductRequest;
import com.microservices.spring.productservice.responses.ProductResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product storeProductRequestToProduct(StoreProductRequest storeProductRequest);

  ProductResponse productToProductResponse(Product product);

}
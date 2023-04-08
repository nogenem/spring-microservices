package com.microservices.spring.productservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.microservices.spring.common.responses.PagedEntityResponse;
import com.microservices.spring.productservicecontracts.requests.StoreProductRequest;
import com.microservices.spring.productservicecontracts.requests.UpdateProductRequest;
import com.microservices.spring.productservicecontracts.responses.ProductResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product storeProductRequestToProduct(StoreProductRequest storeProductRequest);

  ProductResponse productToProductResponse(Product product);

  @Mapping(target = "page", source = "number")
  @Mapping(target = "content", source = "content", defaultExpression = "java(new java.util.ArrayList<>())")
  PagedEntityResponse<ProductResponse> pagedProductsToPagedProductsResponse(Page<Product> pagedProducts);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sku", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateProductFromUpdateProductRequest(UpdateProductRequest updateProductRequest, @MappingTarget Product product);

}
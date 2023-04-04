package com.microservices.spring.inventoryservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.microservices.spring.inventoryservice.exceptions.ApiException;
import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservice.responses.ExceptionResponse;
import com.microservices.spring.inventoryservice.responses.InventoryResponse;
import com.microservices.spring.inventoryservice.responses.PagedInventoryResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Inventory storeInventoryRequestToInventory(StoreInventoryRequest inventoryRequest);

  InventoryResponse inventoryToInventoryResponse(Inventory inventory);

  @Mapping(target = "page", source = "number")
  @Mapping(target = "content", source = "content", defaultExpression = "java(new java.util.ArrayList<>())")
  PagedInventoryResponse pagedInventoryToPagedInventoryResponse(Page<Inventory> pagedInventory);

  @Mapping(target = "stackTrace", ignore = true)
  ExceptionResponse apiExceptionToExceptionResponse(ApiException apiException);

}

package com.microservices.spring.inventoryservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.microservices.spring.inventoryservice.exceptions.ApiException;
import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservice.requests.UpdateInventoryRequest;
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

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sku", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateInventoryFromUpdateInventoryRequest(UpdateInventoryRequest request,
      @MappingTarget Inventory inventory);

  @Mapping(target = "stackTrace", ignore = true)
  ExceptionResponse apiExceptionToExceptionResponse(ApiException apiException);

}
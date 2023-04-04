package com.microservices.spring.inventoryservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservices.spring.inventoryservice.exceptions.ApiException;
import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservice.responses.ExceptionResponse;
import com.microservices.spring.inventoryservice.responses.InventoryResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Inventory storeInventoryRequestToInventory(StoreInventoryRequest inventoryRequest);

  InventoryResponse inventoryToInventoryResponse(Inventory inventory);

  @Mapping(target = "stackTrace", ignore = true)
  ExceptionResponse apiExceptionToExceptionResponse(ApiException apiException);

}

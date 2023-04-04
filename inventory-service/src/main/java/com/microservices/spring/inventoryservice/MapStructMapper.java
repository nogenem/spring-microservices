package com.microservices.spring.inventoryservice;

import org.mapstruct.Mapper;

import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservice.responses.InventoryResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Inventory storeInventoryRequestToInventory(StoreInventoryRequest inventoryRequest);

  InventoryResponse inventoryToInventoryResponse(Inventory inventory);

}

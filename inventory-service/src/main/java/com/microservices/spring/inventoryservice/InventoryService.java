package com.microservices.spring.inventoryservice;

import org.springframework.stereotype.Service;

import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class InventoryService {

  private final InventoryRepository inventoryRepository;
  private final MapStructMapper mapStructMapper;

  public Inventory storeInventory(@Valid StoreInventoryRequest request) {
    Inventory inventory = mapStructMapper.storeInventoryRequestToInventory(request);

    inventory = inventoryRepository.save(inventory);

    log.info("Inventory saved. Id: {} - Sku: {} - Quantity: {}", inventory.getId(), inventory.getSku(),
        inventory.getQuantity());

    return inventory;
  }

}

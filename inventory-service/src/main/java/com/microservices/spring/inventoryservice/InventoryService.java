package com.microservices.spring.inventoryservice;

import org.springframework.dao.DataIntegrityViolationException;
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

    try {
      inventory = inventoryRepository.save(inventory);
    } catch (DataIntegrityViolationException exception) {
      String message = exception.getMostSpecificCause().getMessage();
      throw new RuntimeException(message);
    }

    log.info("Inventory saved. Id: {} - Sku: {} - Quantity: {}", inventory.getId(), inventory.getSku(),
        inventory.getQuantity());

    return inventory;
  }

}

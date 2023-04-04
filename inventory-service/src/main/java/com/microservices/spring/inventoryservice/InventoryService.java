package com.microservices.spring.inventoryservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.spring.inventoryservice.exceptions.InventoryWithThisSkuNotFoundException;
import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservice.requests.UpdateInventoryRequest;

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

  public Page<Inventory> findAll(Pageable pageOptions) {
    return inventoryRepository.findAll(pageOptions);
  }

  public Inventory findBySku(String sku) {
    return inventoryRepository.findBySku(sku)
        .orElseThrow(() -> new InventoryWithThisSkuNotFoundException(sku));
  }

  public Inventory updateInventory(String sku, @Valid UpdateInventoryRequest request) {
    Inventory inventory = findBySku(sku);
    mapStructMapper.updateInventoryFromUpdateInventoryRequest(request, inventory);

    inventory = inventoryRepository.save(inventory);

    log.info("Inventory updated. Id: {} - Sku: {} - New Quantity: {}", inventory.getId(), inventory.getSku(),
        inventory.getQuantity());

    return inventory;
  }

}

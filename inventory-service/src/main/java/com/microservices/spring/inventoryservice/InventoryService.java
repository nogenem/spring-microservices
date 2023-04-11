package com.microservices.spring.inventoryservice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.spring.inventoryservice.exceptions.InventoriesWithTheseSkusNotFoundException;
import com.microservices.spring.inventoryservice.exceptions.InventoryWithThisSkuNotFoundException;
import com.microservices.spring.inventoryservicecontracts.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservicecontracts.requests.UpdateInventoryRequest;

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

  public List<Inventory> findBySkuIn(List<String> skus) {
    List<Inventory> inventories = inventoryRepository.findBySkuIn(skus);

    if (inventories.size() != skus.size()) {
      List<String> databaseSkus = inventories.stream().map(Inventory::getSku).toList();
      List<String> invalidSkus = skus.stream().filter(sku -> !databaseSkus.contains(sku)).toList();

      throw new InventoriesWithTheseSkusNotFoundException(invalidSkus);
    }

    return inventories;
  }

  public Inventory updateInventory(String sku, @Valid UpdateInventoryRequest request) {
    Inventory inventory = findBySku(sku);
    mapStructMapper.updateInventoryFromUpdateInventoryRequest(request, inventory);

    inventory = inventoryRepository.save(inventory);

    log.info("Inventory updated. Id: {} - Sku: {} - New Quantity: {}", inventory.getId(), inventory.getSku(),
        inventory.getQuantity());

    return inventory;
  }

  public void deleteInventory(String sku) {
    Inventory inventory = findBySku(sku);

    inventoryRepository.delete(inventory);

    log.info("Inventory deleted. Id: {} - Sku: {} - Quantity: {}", inventory.getId(), inventory.getSku(),
        inventory.getQuantity());
  }

}

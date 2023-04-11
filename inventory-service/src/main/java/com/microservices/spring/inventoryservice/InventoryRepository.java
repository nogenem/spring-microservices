package com.microservices.spring.inventoryservice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

  Optional<Inventory> findBySku(String sku);

  List<Inventory> findBySkuIn(List<String> skus);

}

package com.microservices.spring.inventoryservice;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

  Optional<Inventory> findBySku(String sku);

}

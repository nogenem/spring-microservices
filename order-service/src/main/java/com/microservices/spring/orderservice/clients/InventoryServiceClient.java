package com.microservices.spring.orderservice.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.spring.inventoryservicecontracts.responses.InventoryQuantityResponse;

@FeignClient("${feign-client.inventory-service.url}/api/inventories")
public interface InventoryServiceClient {

  @GetMapping("/{skus}/quantities")
  List<InventoryQuantityResponse> findInventoriesQuantitiesBySkus(@PathVariable List<String> skus);

}

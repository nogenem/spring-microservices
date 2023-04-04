package com.microservices.spring.inventoryservice.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.inventoryservice.BaseIntegrationTest;
import com.microservices.spring.inventoryservice.Inventory;
import com.microservices.spring.inventoryservice.InventoryRepository;
import com.microservices.spring.inventoryservice.exceptions.InventoryWithThisSkuNotFoundException;
import com.microservices.spring.inventoryservice.factories.FakeInventoryFactory;

public class DeleteInventoryBySkuTest extends BaseIntegrationTest {

  @Autowired
  private InventoryRepository inventoryRepository;

  @Autowired
  private FakeInventoryFactory inventoryFactory;

  @Test
  @DisplayName("Should be able to delete inventories by sku")
  public void shouldBeAbleToDeleteInventoriesBySku() throws JsonProcessingException, Exception {
    Inventory savedInventory = inventoryFactory.createOne();
    inventoryRepository.save(savedInventory);

    ResultActions resultActions = mvc.perform(delete("/api/inventories/{sku}", savedInventory.getSku())
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isNoContent());

    Boolean inventoryExists = inventoryRepository.existsById(savedInventory.getId());
    Assertions.assertFalse(inventoryExists);
  }

  @Test
  @DisplayName("Should not be able to delete inventories with an invalid sku")
  public void shouldNotBeAbleToDeleteInventoriesWithAnInvalidSku() throws JsonProcessingException, Exception {
    Inventory savedInventory = inventoryFactory.createOne();
    inventoryRepository.save(savedInventory);

    String sku = savedInventory.getSku() + "_invalid";
    ResultActions resultActions = mvc.perform(delete("/api/inventories/{sku}", sku)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(InventoryWithThisSkuNotFoundException.CODE));

    Boolean inventoryExists = inventoryRepository.existsById(savedInventory.getId());
    Assertions.assertTrue(inventoryExists);
  }

}

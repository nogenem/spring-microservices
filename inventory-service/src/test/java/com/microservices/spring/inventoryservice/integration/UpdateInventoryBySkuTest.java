package com.microservices.spring.inventoryservice.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.common.exceptions.ValidationErrorsException;
import com.microservices.spring.inventoryservice.BaseIntegrationTest;
import com.microservices.spring.inventoryservice.Inventory;
import com.microservices.spring.inventoryservice.InventoryRepository;
import com.microservices.spring.inventoryservice.exceptions.InventoryWithThisSkuNotFoundException;
import com.microservices.spring.inventoryservice.factories.FakeInventoryFactory;
import com.microservices.spring.inventoryservice.factories.requests.FakeUpdateInventoryRequestFactory;
import com.microservices.spring.inventoryservice.requests.UpdateInventoryRequest;

public class UpdateInventoryBySkuTest extends BaseIntegrationTest {

  @Autowired
  private InventoryRepository inventoryRepository;

  @Autowired
  private FakeInventoryFactory inventoryFactory;

  @Autowired
  private FakeUpdateInventoryRequestFactory updateInventoryFactory;

  @Test
  @DisplayName("Should be able to update inventories by sku")
  public void shouldBeAbleToUpdateInventoriesBySku() throws JsonProcessingException, Exception {
    Inventory savedInventory = inventoryFactory.createOne();
    inventoryRepository.save(savedInventory);

    UpdateInventoryRequest request = updateInventoryFactory.createOne();

    ResultActions resultActions = mvc.perform(put("/api/inventories/{sku}", savedInventory.getSku())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sku").value(savedInventory.getSku()))
        .andExpect(jsonPath("$.quantity").value(request.getQuantity()));

    Optional<Inventory> updatedInventory = inventoryRepository.findById(savedInventory.getId());
    Assertions.assertTrue(updatedInventory.isPresent());
    Assertions.assertEquals(updatedInventory.get().getQuantity(), request.getQuantity());
  }

  @Test
  @DisplayName("Should not be able to update inventories with an invalid sku")
  public void shouldNotBeAbleToUpdateInventoriesWithAnInvalidSku() throws JsonProcessingException, Exception {
    Inventory savedInventory = inventoryFactory.createOne();
    inventoryRepository.save(savedInventory);

    UpdateInventoryRequest request = updateInventoryFactory.createOne();

    String sku = savedInventory.getSku() + "_invalid";
    ResultActions resultActions = mvc.perform(put("/api/inventories/{sku}", sku)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(InventoryWithThisSkuNotFoundException.CODE));

    Optional<Inventory> updatedInventory = inventoryRepository.findById(savedInventory.getId());
    Assertions.assertTrue(updatedInventory.isPresent());
    Assertions.assertNotEquals(updatedInventory.get().getQuantity(), request.getQuantity());
  }

  @Test
  @DisplayName("Should not be able to update inventories with data that dont comply by the validations")
  public void shouldNotBeAbleToUpdateInventoriesWithDataThatDontComplyByTheValidations()
      throws JsonProcessingException, Exception {
    Inventory savedInventory = inventoryFactory.createOne();
    inventoryRepository.save(savedInventory);

    UpdateInventoryRequest request = UpdateInventoryRequest.builder()
        .quantity(0)
        .build();

    ResultActions resultActions = mvc.perform(put("/api/inventories/{sku}", savedInventory.getSku())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.quantity").isNotEmpty());

    Optional<Inventory> updatedInventory = inventoryRepository.findById(savedInventory.getId());
    Assertions.assertTrue(updatedInventory.isPresent());
    Assertions.assertNotEquals(updatedInventory.get().getQuantity(), request.getQuantity());
  }

}

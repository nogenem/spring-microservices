package com.microservices.spring.inventoryservice.integration.inventory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.microservices.spring.inventoryservice.factories.FakeInventoryFactory;
import com.microservices.spring.inventoryservice.factories.requests.FakeStoreInventoryRequestFactory;
import com.microservices.spring.inventoryservicecontracts.requests.StoreInventoryRequest;

public class StoreInventoryTest extends BaseIntegrationTest {

  @Autowired
  InventoryRepository inventoryRepository;

  @Autowired
  private FakeInventoryFactory inventoryFactory;

  @Autowired
  private FakeStoreInventoryRequestFactory storeInventoryRequestFactory;

  @Test
  @DisplayName("Should be able to store inventories")
  public void shouldBeAbleToStoreInventories() throws JsonProcessingException, Exception {
    StoreInventoryRequest request = storeInventoryRequestFactory.createOne();

    ResultActions resultActions = mvc.perform(post("/api/inventories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.sku").value(request.getSku()))
        .andExpect(jsonPath("$.id").isNotEmpty());

    Assertions.assertEquals(1, inventoryRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to store inventory with a non unique sku")
  public void shouldNotBeAbleToStoreInventoryWithANonUniqueSku() throws JsonProcessingException, Exception {
    String sku = "SP-001";

    Inventory savedInventory = inventoryFactory.createOne();
    savedInventory.setSku(sku);
    inventoryRepository.save(savedInventory);

    StoreInventoryRequest request = storeInventoryRequestFactory.createOne();
    request.setSku(sku);

    ResultActions resultActions = mvc.perform(post("/api/inventories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE));

    Assertions.assertEquals(1, inventoryRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to store inventory that dont comply by thevalidations")
  public void shouldNotBeAbleToStoreInventoryThatDontComplyByTheValidations()
      throws JsonProcessingException, Exception {
    StoreInventoryRequest request = StoreInventoryRequest.builder()
        .sku("So")
        .quantity(0)
        .build();

    ResultActions resultActions = mvc.perform(post("/api/inventories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.sku").isNotEmpty())
        .andExpect(jsonPath("$.errors.quantity").isNotEmpty());

    Assertions.assertEquals(0, inventoryRepository.findAll().size());
  }

}

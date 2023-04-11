package com.microservices.spring.inventoryservice.integration.inventory;

import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.inventoryservice.BaseIntegrationTest;
import com.microservices.spring.inventoryservice.Inventory;
import com.microservices.spring.inventoryservice.InventoryRepository;
import com.microservices.spring.inventoryservice.exceptions.InventoriesWithTheseSkusNotFoundException;
import com.microservices.spring.inventoryservice.factories.FakeInventoryFactory;

public class FindInventoriesQuantitiesBySkusTest extends BaseIntegrationTest {

  @Autowired
  private InventoryRepository inventoryRepository;

  @Autowired
  private FakeInventoryFactory inventoryFactory;

  @Test
  @DisplayName("Should be able to get inventories quantities by skus")
  public void shouldBeAbleToGetInventoriesQuantitiesBySkus() throws JsonProcessingException, Exception {
    List<Inventory> savedInventories = inventoryFactory.createMany(2);
    inventoryRepository.saveAll(savedInventories);

    List<String> skus = savedInventories.stream().map(Inventory::getSku).toList();

    ResultActions resultActions = mvc.perform(get("/api/inventories/{skus}/quantities", String.join(",", skus))
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(savedInventories.size()))
        .andExpect(jsonPath("$[0].sku").value(in(skus)))
        .andExpect(jsonPath("$[1].sku").value(in(skus)));
  }

  @Test
  @DisplayName("Should not be able to get inventories quantities with invalid skus")
  public void shouldNotBeAbleToGetInventoriesQuantitiesWithInvalidSkus() throws JsonProcessingException, Exception {
    List<Inventory> savedInventories = inventoryFactory.createMany(2);
    inventoryRepository.saveAll(savedInventories);

    List<String> skus = savedInventories.stream().map(i -> i.getSku() + "_invalid").toList();

    ResultActions resultActions = mvc.perform(get("/api/inventories/{skus}/quantities", String.join(",", skus))
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(InventoriesWithTheseSkusNotFoundException.CODE));
  }

}

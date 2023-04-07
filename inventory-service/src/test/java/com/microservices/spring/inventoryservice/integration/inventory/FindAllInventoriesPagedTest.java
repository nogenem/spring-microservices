package com.microservices.spring.inventoryservice.integration.inventory;

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
import com.microservices.spring.inventoryservice.factories.FakeInventoryFactory;

public class FindAllInventoriesPagedTest extends BaseIntegrationTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private FakeInventoryFactory inventoryFactory;

    @Test
    @DisplayName("Should be able to get saved inventories")
    public void shouldBeAbleToGetSavedInventories() throws JsonProcessingException, Exception {
        List<Inventory> savedInventories = inventoryFactory.createMany(2);
        inventoryRepository.saveAll(savedInventories);

        ResultActions resultActions = mvc.perform(get("/api/inventories")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(savedInventories.size()));
    }

    @Test
    @DisplayName("Should be able to control the page index and size of the returned inventories")
    public void shouldBeAbleToControlThePageIndexAndSizeOfTheReturnedInventories()
            throws JsonProcessingException, Exception {
        List<Inventory> savedInventories = inventoryFactory.createMany(7);
        inventoryRepository.saveAll(savedInventories);

        // First page
        int page = 0;
        int size = 5;
        ResultActions resultActions = mvc.perform(get("/api/inventories")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(size))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.page").value(page));

        // Second page
        page = 1;
        resultActions = mvc.perform(get("/api/inventories")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(savedInventories.size() - size))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.page").value(page));
    }

    @Test
    @DisplayName("Should be able to control the sorting of the returned inventories")
    public void shouldBeAbleToControlTheSortingOfTheReturnedInventories() throws JsonProcessingException, Exception {
        Inventory firstSavedInventory = inventoryFactory.createOne();
        inventoryRepository.save(firstSavedInventory);

        Inventory lastSavedInventory = inventoryFactory.createOne();
        inventoryRepository.save(lastSavedInventory);

        // ASC order
        ResultActions resultActions = mvc.perform(get("/api/inventories")
                .param("sort", "createdAt")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].sku").value(firstSavedInventory.getSku()));

        // DESC order
        resultActions = mvc.perform(get("/api/inventories")
                .param("sort", "-createdAt")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].sku").value(lastSavedInventory.getSku()));
    }

}

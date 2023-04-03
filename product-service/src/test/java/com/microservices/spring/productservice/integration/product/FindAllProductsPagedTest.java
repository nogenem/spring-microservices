package com.microservices.spring.productservice.integration.product;

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
import com.microservices.spring.productservice.BaseIntegrationTest;
import com.microservices.spring.productservice.Product;
import com.microservices.spring.productservice.ProductRepository;
import com.microservices.spring.productservice.factories.FakeProductFactory;

public class FindAllProductsPagedTest extends BaseIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private FakeProductFactory productFactory;

  @Test
  @DisplayName("Should be able to get saved products")
  public void shouldBeAbleToGetSavedProducts() throws JsonProcessingException, Exception {
    List<Product> savedProducts = productFactory.createMany(2);
    productRepository.saveAll(savedProducts);

    ResultActions resultActions = mvc.perform(get("/api/products")
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(savedProducts.size()));
  }

  @Test
  @DisplayName("Should be able to control the page index and size of the returned products")
  public void shouldBeAbleToControlThePageIndexAndSizeOfTheReturnedProducts()
      throws JsonProcessingException, Exception {
    List<Product> savedProducts = productFactory.createMany(7);
    productRepository.saveAll(savedProducts);

    // First page
    int page = 0;
    int size = 5;
    ResultActions resultActions = mvc.perform(get("/api/products")
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
    resultActions = mvc.perform(get("/api/products")
        .param("page", String.valueOf(page))
        .param("size", String.valueOf(size))
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(savedProducts.size() - size))
        .andExpect(jsonPath("$.first").value(false))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.page").value(page));
  }

  @Test
  @DisplayName("Should be able to control the sorting of the returned products")
  public void shouldBeAbleToControlTheSortingOfTheReturnedProducts() throws JsonProcessingException, Exception {
    Product firstSavedProduct = productFactory.createOne();
    productRepository.save(firstSavedProduct);

    Product lastSavedProduct = productFactory.createOne();
    productRepository.save(lastSavedProduct);

    // ASC order
    ResultActions resultActions = mvc.perform(get("/api/products")
        .param("sort", "createdAt")
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].id").value(firstSavedProduct.getId()));

    // DESC order
    resultActions = mvc.perform(get("/api/products")
        .param("sort", "-createdAt")
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].id").value(lastSavedProduct.getId()));
  }

}

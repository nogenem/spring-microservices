package com.microservices.spring.productservice.integration.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.productservice.BaseIntegrationTest;
import com.microservices.spring.productservice.Product;
import com.microservices.spring.productservice.ProductRepository;
import com.microservices.spring.productservice.exceptions.ProductWithThisSkuNotFoundException;
import com.microservices.spring.productservice.factories.FakeProductFactory;

public class FindProductBySkuTest extends BaseIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private FakeProductFactory productFactory;

  @Test
  @DisplayName("Should be able to get products by sku")
  public void shouldBeAbleToGetProductsBySku() throws JsonProcessingException, Exception {
    Product savedProduct = productFactory.createOne();
    productRepository.save(savedProduct);

    ResultActions resultActions = mvc.perform(get("/api/products/{sku}", savedProduct.getSku())
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedProduct.getId()))
        .andExpect(jsonPath("$.sku").value(savedProduct.getSku()))
        .andExpect(jsonPath("$.slug").value(savedProduct.getSlug()));
  }

  @Test
  @DisplayName("Should not be able to get products with an invalid sku")
  public void shouldNotBeAbleToGetProductsWithAnInvalidSku() throws JsonProcessingException, Exception {
    Product savedProduct = productFactory.createOne();
    productRepository.save(savedProduct);

    String sku = savedProduct.getSku() + "_invalid";
    ResultActions resultActions = mvc.perform(get("/api/products/{sku}", sku)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ProductWithThisSkuNotFoundException.CODE));
  }

}
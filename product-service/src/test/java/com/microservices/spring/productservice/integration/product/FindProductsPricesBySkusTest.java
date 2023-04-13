package com.microservices.spring.productservice.integration.product;

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
import com.microservices.spring.productservice.BaseIntegrationTest;
import com.microservices.spring.productservice.Product;
import com.microservices.spring.productservice.ProductRepository;
import com.microservices.spring.productservice.exceptions.ProductsWithTheseSkusNotFoundException;
import com.microservices.spring.productservice.factories.FakeProductFactory;

public class FindProductsPricesBySkusTest extends BaseIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private FakeProductFactory productFactory;

  @Test
  @DisplayName("Should be able to get products prices by sku")
  public void shouldBeAbleToGetProductsPricesBySku() throws JsonProcessingException, Exception {
    List<Product> savedProducts = productFactory.createMany(2);
    productRepository.saveAll(savedProducts);

    List<String> skus = savedProducts.stream().map(Product::getSku).toList();
    List<Integer> prices = savedProducts.stream().map(Product::getPrice).toList();

    ResultActions resultActions = mvc.perform(get("/api/products/{skus}/prices", String.join(",", skus))
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(savedProducts.size()))
        .andExpect(jsonPath("$[0].sku").value(in(skus)))
        .andExpect(jsonPath("$[1].sku").value(in(skus)))
        .andExpect(jsonPath("$[0].price").value(in(prices)))
        .andExpect(jsonPath("$[1].price").value(in(prices)));
  }

  @Test
  @DisplayName("Should not be able to get products prices with invalid skus")
  public void shouldNotBeAbleToGetProductsPricesWithInvalidSkus() throws JsonProcessingException, Exception {
    List<Product> savedProducts = productFactory.createMany(2);
    productRepository.saveAll(savedProducts);

    List<String> skus = savedProducts.stream().map(p -> p.getSku() + "_invalid").toList();

    ResultActions resultActions = mvc.perform(get("/api/products/{skus}/prices", String.join(",", skus))
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ProductsWithTheseSkusNotFoundException.CODE));
  }

}

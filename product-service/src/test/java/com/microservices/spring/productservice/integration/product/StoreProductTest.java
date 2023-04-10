package com.microservices.spring.productservice.integration.product;

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
import com.microservices.spring.productservice.BaseIntegrationTest;
import com.microservices.spring.productservice.Product;
import com.microservices.spring.productservice.ProductRepository;
import com.microservices.spring.productservice.factories.FakeProductFactory;
import com.microservices.spring.productservice.factories.requests.FakeStoreProductRequestFactory;
import com.microservices.spring.productservicecontracts.requests.StoreProductRequest;

public class StoreProductTest extends BaseIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private FakeProductFactory productFactory;

  @Autowired
  private FakeStoreProductRequestFactory storeProductRequestFactory;

  @Test
  @DisplayName("Should be able to store products")
  public void shouldBeAbleToStoreProducts() throws JsonProcessingException, Exception {
    StoreProductRequest request = storeProductRequestFactory.createOne();

    ResultActions resultActions = mvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.slug").value(request.getSlug()))
        .andExpect(jsonPath("$.id").isNotEmpty());

    Assertions.assertEquals(1, productRepository.findAll().size());
  }

  @Test
  @DisplayName("Should slugify invalid slugs when storing products")
  public void shouldSlugifyInvalidSlugsWhenStoringProducts() throws JsonProcessingException, Exception {
    // Null slug
    StoreProductRequest request = storeProductRequestFactory.createOne();
    request.setSlug(null);

    ResultActions resultActions = mvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.slug").isNotEmpty())
        .andExpect(jsonPath("$.id").isNotEmpty());

    Assertions.assertEquals(1, productRepository.findAll().size());

    // Invalid slug
    request = storeProductRequestFactory.createOne();
    request.setSlug("INVALID SLUG");

    resultActions = mvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.slug").isNotEmpty())
        .andExpect(jsonPath("$.id").isNotEmpty());

    Assertions.assertEquals(2, productRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to store product with a non unique slug")
  public void shouldNotBeAbleToStoreProductWithANonUniqueSlug() throws JsonProcessingException, Exception {
    String slug = "some-product";

    Product savedProduct = productFactory.createOne();
    savedProduct.setSlug(slug);
    productRepository.save(savedProduct);

    StoreProductRequest request = storeProductRequestFactory.createOne();
    request.setSlug(slug);

    ResultActions resultActions = mvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE));

    Assertions.assertEquals(1, productRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to store product with a non unique sku")
  public void shouldNotBeAbleToStoreProductWithANonUniqueSku() throws JsonProcessingException, Exception {
    String sku = "SP-001";

    Product savedProduct = productFactory.createOne();
    savedProduct.setSku(sku);
    productRepository.save(savedProduct);

    StoreProductRequest request = storeProductRequestFactory.createOne();
    request.setSku(sku);

    ResultActions resultActions = mvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE));

    Assertions.assertEquals(1, productRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to store product that dont comply by the validations")
  public void shouldNotBeAbleToStoreProductThatDontComplyByTheValidations() throws JsonProcessingException, Exception {
    StoreProductRequest request = StoreProductRequest.builder()
        .slug("so")
        .name("So")
        .price(0)
        .build();

    ResultActions resultActions = mvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.slug").isNotEmpty())
        .andExpect(jsonPath("$.errors.name").isNotEmpty())
        .andExpect(jsonPath("$.errors.price").isNotEmpty());

    Assertions.assertEquals(0, productRepository.findAll().size());
  }

}
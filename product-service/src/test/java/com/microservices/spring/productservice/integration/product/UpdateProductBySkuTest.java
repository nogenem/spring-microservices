package com.microservices.spring.productservice.integration.product;

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
import com.microservices.spring.productservice.BaseIntegrationTest;
import com.microservices.spring.productservice.Product;
import com.microservices.spring.productservice.ProductRepository;
import com.microservices.spring.productservice.exceptions.ProductWithThisSkuNotFoundException;
import com.microservices.spring.productservice.exceptions.ValidationErrorsException;
import com.microservices.spring.productservice.factories.FakeProductFactory;
import com.microservices.spring.productservice.factories.requests.FakeUpdateProductRequestFactory;
import com.microservices.spring.productservice.requests.UpdateProductRequest;

public class UpdateProductBySkuTest extends BaseIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private FakeProductFactory productFactory;

  @Autowired
  private FakeUpdateProductRequestFactory updateProductFactory;

  @Test
  @DisplayName("Should be able to update products by sku")
  public void shouldBeAbleToUpdateProductsBySku() throws JsonProcessingException, Exception {
    Product savedProduct = productFactory.createOne();
    productRepository.save(savedProduct);

    UpdateProductRequest request = updateProductFactory.createOne();

    ResultActions resultActions = mvc.perform(put("/api/products/{sku}", savedProduct.getSku())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedProduct.getId()))
        .andExpect(jsonPath("$.sku").value(savedProduct.getSku()))
        .andExpect(jsonPath("$.slug").value(request.getSlug()))
        .andExpect(jsonPath("$.name").value(request.getName()));

    Optional<Product> updatedProduct = productRepository.findById(savedProduct.getId());
    Assertions.assertTrue(updatedProduct.isPresent());
    Assertions.assertEquals(updatedProduct.get().getSlug(), request.getSlug());
    Assertions.assertEquals(updatedProduct.get().getName(), request.getName());
  }

  @Test
  @DisplayName("Should slugify invalid slugs when updating products")
  public void shouldSlugifyInvalidSlugsWhenUpdatingProducts() throws JsonProcessingException, Exception {
    Product savedProduct = productFactory.createOne();
    productRepository.save(savedProduct);

    // Null slug
    UpdateProductRequest request = updateProductFactory.createOne();
    request.setSlug(null);

    ResultActions resultActions = mvc.perform(put("/api/products/{sku}", savedProduct.getSku())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedProduct.getId()))
        .andExpect(jsonPath("$.sku").value(savedProduct.getSku()))
        .andExpect(jsonPath("$.slug").isNotEmpty())
        .andExpect(jsonPath("$.name").value(request.getName()));

    Optional<Product> updatedProduct = productRepository.findById(savedProduct.getId());
    Assertions.assertTrue(updatedProduct.isPresent());
    Assertions.assertNotNull(updatedProduct.get().getSlug());
    Assertions.assertEquals(updatedProduct.get().getName(), request.getName());

    // Invalid slug
    request = updateProductFactory.createOne();
    request.setSlug("INVALID SLUG");

    resultActions = mvc.perform(put("/api/products/{sku}", savedProduct.getSku())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedProduct.getId()))
        .andExpect(jsonPath("$.sku").value(savedProduct.getSku()))
        .andExpect(jsonPath("$.slug").isNotEmpty())
        .andExpect(jsonPath("$.name").value(request.getName()));

    updatedProduct = productRepository.findById(savedProduct.getId());
    Assertions.assertTrue(updatedProduct.isPresent());
    Assertions.assertNotNull(updatedProduct.get().getSlug());
    Assertions.assertEquals(updatedProduct.get().getName(), request.getName());
  }

  @Test
  @DisplayName("Should not be able to update products with an invalid sku")
  public void shouldNotBeAbleToUpdateProductsWithAnInvalidSku() throws JsonProcessingException, Exception {
    Product savedProduct = productFactory.createOne();
    productRepository.save(savedProduct);

    UpdateProductRequest request = updateProductFactory.createOne();

    String sku = savedProduct.getSku() + "_invalid";
    ResultActions resultActions = mvc.perform(put("/api/products/{sku}", sku)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ProductWithThisSkuNotFoundException.CODE));

    Optional<Product> updatedProduct = productRepository.findById(savedProduct.getId());
    Assertions.assertTrue(updatedProduct.isPresent());
    Assertions.assertNotEquals(updatedProduct.get().getSlug(), request.getSlug());
    Assertions.assertNotEquals(updatedProduct.get().getName(), request.getName());
  }

  @Test
  @DisplayName("Should not be able to update products with a non unique slug")
  public void shouldNotBeAbleToUpdateProductsWithANonUniqueSlug() throws JsonProcessingException, Exception {
    Product savedProduct1 = productFactory.createOne();
    productRepository.save(savedProduct1);

    Product savedProduct2 = productFactory.createOne();
    productRepository.save(savedProduct2);

    String slug = savedProduct1.getSlug();

    UpdateProductRequest request = updateProductFactory.createOne();
    request.setSlug(slug);

    ResultActions resultActions = mvc.perform(put("/api/products/{sku}", savedProduct2.getSku())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE));

    Optional<Product> updatedProduct = productRepository.findById(savedProduct2.getId());
    Assertions.assertTrue(updatedProduct.isPresent());
    Assertions.assertNotEquals(updatedProduct.get().getName(), request.getName());
  }

  @Test
  @DisplayName("Should not be able to update products with data that dont comply by the validations")
  public void shouldNotBeAbleToUpdateProductsWithDataThatDontComplyByTheValidations()
      throws JsonProcessingException, Exception {
    Product savedProduct = productFactory.createOne();
    productRepository.save(savedProduct);

    UpdateProductRequest request = UpdateProductRequest.builder()
        .slug("so")
        .name("So")
        .price(0L)
        .build();

    ResultActions resultActions = mvc.perform(put("/api/products/{sku}", savedProduct.getSku())
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

    Optional<Product> updatedProduct = productRepository.findById(savedProduct.getId());
    Assertions.assertTrue(updatedProduct.isPresent());
    Assertions.assertNotEquals(updatedProduct.get().getSlug(), request.getSlug());
    Assertions.assertNotEquals(updatedProduct.get().getName(), request.getName());
  }

}

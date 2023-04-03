package com.microservices.spring.productservice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.productservice.requests.StoreProductRequest;
import com.microservices.spring.productservice.responses.ProductResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
@Validated
public class ProductController {

  private final ProductService productService;
  private final MapStructMapper mapStructMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponse storeProduct(@Valid @RequestBody StoreProductRequest request) {
    Product product = productService.storeProduct(request);

    return mapStructMapper.productToProductResponse(product);
  }

}

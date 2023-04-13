package com.microservices.spring.productservice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {

  Optional<Product> findBySku(String sku);

  List<Product> findBySkuIn(List<String> skus);

}

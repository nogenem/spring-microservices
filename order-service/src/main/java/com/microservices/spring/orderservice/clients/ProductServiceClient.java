package com.microservices.spring.orderservice.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.spring.productservicecontracts.responses.ProductPriceResponse;

@FeignClient("${feign-client.product-service.url}/api/products")
public interface ProductServiceClient {

  @GetMapping("/{skus}/prices")
  List<ProductPriceResponse> findProductsPricesBySkus(@PathVariable List<String> skus);

}

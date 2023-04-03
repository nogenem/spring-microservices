package com.microservices.spring.productservice;

import org.springframework.stereotype.Service;

import com.github.slugify.Slugify;
import com.microservices.spring.productservice.requests.StoreProductRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

  private final ProductRepository productRepository;
  private final MapStructMapper mapStructMapper;

  public Product storeProduct(StoreProductRequest request) {
    String slug = getValidSlug(request.getSlug(), request.getName());
    request.setSlug(slug);

    Product product = mapStructMapper.storeProductRequestToProduct(request);

    product = productRepository.save(product);

    log.info("Product saved. Id: {} - Sku: {}", product.getId(), product.getSku());

    return product;
  }

  private String getValidSlug(String slug, String name) {
    final Slugify slg = Slugify.builder().build();

    if (slug == null || slug.isEmpty()) {
      slug = slg.slugify(name);
    } else {
      slug = slg.slugify(slug);
    }

    return slug;
  }

}

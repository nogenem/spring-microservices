package com.microservices.spring.productservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.common.responses.PagedEntityResponse;
import com.microservices.spring.productservicecontracts.requests.StoreProductRequest;
import com.microservices.spring.productservicecontracts.requests.UpdateProductRequest;
import com.microservices.spring.productservicecontracts.responses.ProductResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
@Validated
@Tag(name = "Product API")
public class ProductController {

  private final ProductService productService;
  private final MapStructMapper mapStructMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Store a product")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Product stored"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public ProductResponse storeProduct(@Valid @RequestBody StoreProductRequest request) {
    Product product = productService.storeProduct(request);

    return mapStructMapper.productToProductResponse(product);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get a page of products")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "A page of products"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public PagedEntityResponse<ProductResponse> findAllProductsPaged(
      @RequestParam(defaultValue = "0") @Min(value = 0) int page,
      @RequestParam(defaultValue = "30") @Min(value = 5) int size,
      @RequestParam(defaultValue = "-createdAt") String sort) {
    Sort sortBy = getSortBy(sort);
    Pageable pageOptions = PageRequest.of(page, size, sortBy);

    Page<Product> pagedProducts = productService.findAll(pageOptions);

    return mapStructMapper.pagedProductsToPagedProductsResponse(pagedProducts);
  }

  @GetMapping("/{sku}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get a product by its sku")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The product found"),
      @ApiResponse(responseCode = "404", description = "Product with this sku wasn't found", content = @Content) })
  public ProductResponse findProductBySku(@PathVariable("sku") String sku) {
    Product product = productService.findBySku(sku);

    return mapStructMapper.productToProductResponse(product);
  }

  @PutMapping("/{sku}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update a product by its sku")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The updated product"),
      @ApiResponse(responseCode = "404", description = "Product with this sku wasn't found", content = @Content),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public ProductResponse updateProductBySku(@PathVariable("sku") String sku,
      @Valid @RequestBody UpdateProductRequest request) {
    Product product = productService.updateProduct(sku, request);

    return mapStructMapper.productToProductResponse(product);
  }

  @DeleteMapping("/{sku}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a product by its sku")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "404", description = "Product with this sku wasn't found", content = @Content) })
  public void deleteProductBySku(@PathVariable("sku") String sku) {
    productService.deleteProduct(sku);
  }

  private Sort getSortBy(String sort) {
    Sort.Direction dir = Sort.Direction.ASC;
    if (sort.startsWith("-")) {
      dir = Sort.Direction.DESC;
      sort = sort.substring(1);
    } else if (sort.startsWith("+")) {
      sort = sort.substring(1);
    }

    return Sort.by(dir, sort);
  }

}

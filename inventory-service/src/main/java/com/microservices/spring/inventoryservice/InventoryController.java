package com.microservices.spring.inventoryservice;

import java.util.List;

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
import com.microservices.spring.inventoryservicecontracts.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservicecontracts.requests.UpdateInventoryRequest;
import com.microservices.spring.inventoryservicecontracts.responses.InventoryQuantityResponse;
import com.microservices.spring.inventoryservicecontracts.responses.InventoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/inventories")
@Validated
@Tag(name = "Inventory API")
public class InventoryController {

  private final InventoryService inventoryService;
  private final MapStructMapper mapStructMapper;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Store an inventory")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Inventory stored"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public InventoryResponse storeInventory(@Valid @RequestBody StoreInventoryRequest request) {
    Inventory inventory = inventoryService.storeInventory(request);

    return mapStructMapper.inventoryToInventoryResponse(inventory);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get a page of inventories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "A page of inventories"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public PagedEntityResponse<InventoryResponse> findAllInventoriesPaged(
      @RequestParam(defaultValue = "0") @Min(value = 0) int page,
      @RequestParam(defaultValue = "30") @Min(value = 5) int size,
      @RequestParam(defaultValue = "-createdAt") String sort) {
    Sort sortBy = getSortBy(sort);
    Pageable pageOptions = PageRequest.of(page, size, sortBy);

    Page<Inventory> pagedInventory = inventoryService.findAll(pageOptions);

    return mapStructMapper.pagedInventoryToPagedInventoryResponse(pagedInventory);
  }

  @GetMapping("/{sku}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get an inventory by its sku")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The inventory found"),
      @ApiResponse(responseCode = "404", description = "Inventory with this sku wasn't found", content = @Content) })
  public InventoryResponse findInventoryBySku(@PathVariable("sku") String sku) {
    Inventory inventory = inventoryService.findBySku(sku);

    return mapStructMapper.inventoryToInventoryResponse(inventory);
  }

  @PutMapping("/{sku}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update an inventory by its sku")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The updated inventory"),
      @ApiResponse(responseCode = "404", description = "Inventory with this sku wasn't found", content = @Content),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public InventoryResponse updateInventoryBySku(@PathVariable("sku") String sku,
      @Valid @RequestBody UpdateInventoryRequest request) {
    Inventory inventory = inventoryService.updateInventory(sku, request);

    return mapStructMapper.inventoryToInventoryResponse(inventory);
  }

  @DeleteMapping("/{sku}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete an inventory by its sku")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204"),
      @ApiResponse(responseCode = "404", description = "Inventory with this sku wasn't found", content = @Content) })
  public void deleteInventoryBySku(@PathVariable("sku") String sku) {
    inventoryService.deleteInventory(sku);
  }

  @GetMapping("/{skus}/quantities")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get inventories quantities by skus")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The inventories quantities found"),
      @ApiResponse(responseCode = "404", description = "Inventories with these skus weren't found", content = @Content) })
  @Parameter(in = ParameterIn.PATH, name = "skus", example = "SKU1,SKU2,SKU3")
  public List<InventoryQuantityResponse> findInventoriesQuantitiesBySkus(@PathVariable List<String> skus) {
    List<Inventory> inventories = inventoryService.findBySkuIn(skus);

    return mapStructMapper.inventoriesToInventoryQuantityResponses(inventories);
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

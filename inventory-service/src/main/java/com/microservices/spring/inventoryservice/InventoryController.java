package com.microservices.spring.inventoryservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservice.responses.InventoryResponse;
import com.microservices.spring.inventoryservice.responses.PagedInventoryResponse;

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
@RequestMapping("/api/inventory")
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
  public PagedInventoryResponse findAllInventoriesPaged(
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

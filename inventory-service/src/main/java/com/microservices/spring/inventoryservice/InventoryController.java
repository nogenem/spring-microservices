package com.microservices.spring.inventoryservice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.inventoryservice.requests.StoreInventoryRequest;
import com.microservices.spring.inventoryservice.responses.InventoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

}

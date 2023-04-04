package com.microservices.spring.inventoryservice.exceptions;

import org.springframework.http.HttpStatus;

public class InventoryWithThisSkuNotFoundException extends ApiException {

  public final static String CODE = "INVENTORY_WITH_THIS_SKU_NOT_FOUND";

  public InventoryWithThisSkuNotFoundException(String sku) {
    super(HttpStatus.NOT_FOUND.value(), CODE,
        String.format("An inventory with the sku: %s wasn't found.", sku));
  }

}

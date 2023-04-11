package com.microservices.spring.inventoryservice.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class InventoriesWithTheseSkusNotFoundException extends ApiException {

  public final static String CODE = "INVENTORIES_WITH_THESE_SKUS_NOT_FOUND";

  public InventoriesWithTheseSkusNotFoundException(List<String> skus) {
    super(HttpStatus.NOT_FOUND.value(), CODE,
        String.format("Inventories with the skus: %s weren't found.", skus));
  }

}

package com.microservices.spring.inventoryservice.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ApiException extends RuntimeException {

  public final static String CODE = "API_EXCEPTION";

  @Getter
  @Setter
  private Integer status;

  @Getter
  @Setter
  private String code;

  @Getter
  @Setter
  private ValidationErrorsMap errors;

  public ApiException(Integer status, String code, String message) {
    this(status, code, message, null);
  }

  public ApiException(Integer status, String code, String message, ValidationErrorsMap errors) {
    super(message);
    this.status = status;
    this.code = code;
    this.errors = errors;
  }

  public static class ValidationErrorsMap extends HashMap<String, List<String>> {
    public void put(String key, String message) {
      if (!this.containsKey(key)) {
        this.put(key, new ArrayList<>());
      }

      this.get(key).add(message);
    }
  }

}
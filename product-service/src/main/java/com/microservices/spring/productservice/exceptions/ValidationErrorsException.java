package com.microservices.spring.productservice.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationErrorsException extends ApiException {

  public final static String CODE = "VALIDATION_ERRORS";

  public ValidationErrorsException(ValidationErrorsMap errors) {
    super(HttpStatus.UNPROCESSABLE_ENTITY.value(), CODE, "Validation errors", errors);
  }

}
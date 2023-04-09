package com.microservices.spring.authservice.exceptions;

import com.microservices.spring.common.exceptions.ValidationErrorsException;

public class InvalidCredentialsException extends ValidationErrorsException {

  public InvalidCredentialsException() {
    super(null);

    ValidationErrorsMap errors = new ValidationErrorsMap();
    errors.put("global", "invalid credentials");

    setErrors(errors);
  }

}

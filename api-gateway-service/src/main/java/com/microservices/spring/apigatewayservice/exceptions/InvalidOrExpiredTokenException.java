package com.microservices.spring.apigatewayservice.exceptions;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class InvalidOrExpiredTokenException extends ApiException {

  public final static String CODE = "INVALID_OR_EXPIRED_TOKEN";

  public InvalidOrExpiredTokenException() {
    super(HttpStatus.BAD_REQUEST.value(), CODE, "Invalid or expired token");
  }

}

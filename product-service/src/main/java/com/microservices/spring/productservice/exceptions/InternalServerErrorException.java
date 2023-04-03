package com.microservices.spring.productservice.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApiException {

  public final static String CODE = "INTERNAL_SERVER_ERROR";

  public InternalServerErrorException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR.value(), CODE, "Internal server error");
  }

}

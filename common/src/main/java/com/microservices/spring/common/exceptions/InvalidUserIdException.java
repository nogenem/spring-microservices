package com.microservices.spring.common.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidUserIdException extends ApiException {

  public final static String CODE = "INVALID_USER_ID";

  public InvalidUserIdException(String userId) {
    super(HttpStatus.BAD_REQUEST.value(), CODE,
        String.format("Invalid user_id: %s.", userId));
  }

}

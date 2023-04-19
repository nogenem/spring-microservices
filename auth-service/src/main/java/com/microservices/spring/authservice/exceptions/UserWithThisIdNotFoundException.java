package com.microservices.spring.authservice.exceptions;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.microservices.spring.common.exceptions.ApiException;

public class UserWithThisIdNotFoundException extends ApiException {

  public final static String CODE = "USER_WITH_THIS_ID_NOT_FOUND";

  public UserWithThisIdNotFoundException(UUID userId) {
    super(HttpStatus.NOT_FOUND.value(), CODE,
        String.format("An user with the id: %s wasn't found.", userId.toString()));
  }

}

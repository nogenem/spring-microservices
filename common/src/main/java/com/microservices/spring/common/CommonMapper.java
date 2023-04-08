package com.microservices.spring.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservices.spring.common.exceptions.ApiException;
import com.microservices.spring.common.responses.ExceptionResponse;

@Mapper(componentModel = "spring")
public interface CommonMapper {

  @Mapping(target = "stackTrace", ignore = true)
  ExceptionResponse apiExceptionToExceptionResponse(ApiException apiException);

}

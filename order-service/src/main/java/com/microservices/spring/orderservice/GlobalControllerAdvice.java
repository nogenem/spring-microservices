package com.microservices.spring.orderservice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.microservices.spring.common.exceptions.ApiException;
import com.microservices.spring.common.exceptions.ApiException.ValidationErrorsMap;
import com.microservices.spring.common.exceptions.InternalServerErrorException;
import com.microservices.spring.common.exceptions.ValidationErrorsException;
import com.microservices.spring.common.responses.ExceptionResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

  @Value("${responses.exceptions.return-stack-trace:false}")
  private Boolean returnStackTrace;

  private final MapStructMapper mapStructMapper;

  // PS: Can't use `@AllArgsConstructor` cause of the `@Value`
  public GlobalControllerAdvice(MapStructMapper mapStructMapper) {
    this.mapStructMapper = mapStructMapper;
  }

  @ExceptionHandler({ ApiException.class })
  @ResponseBody
  public ResponseEntity<ExceptionResponse> onApiException(ApiException baseException) {
    return buildExceptionResponse(baseException, baseException);
  }

  @ExceptionHandler({ ConstraintViolationException.class })
  @ResponseBody
  public ResponseEntity<ExceptionResponse> onConstraintViolationException(ConstraintViolationException baseException) {
    ValidationErrorsMap errors = new ValidationErrorsMap();

    for (final ConstraintViolation<?> violation : baseException.getConstraintViolations()) {
      // Get only the last part of the path
      String[] paths = violation.getPropertyPath().toString().split("\\.");
      errors.put(paths[paths.length - 1], violation.getMessage());
    }

    ValidationErrorsException apiException = new ValidationErrorsException(errors);

    return buildExceptionResponse(apiException, baseException);
  }

  @ExceptionHandler({ MethodArgumentNotValidException.class })
  @ResponseBody()
  public ResponseEntity<ExceptionResponse> onMethodArgumentNotValidException(
      MethodArgumentNotValidException baseException) {
    ValidationErrorsMap errors = new ValidationErrorsMap();

    for (final FieldError fieldError : baseException.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    for (final ObjectError globalError : baseException.getBindingResult().getGlobalErrors()) {
      errors.put("global", globalError.getDefaultMessage());
    }

    ValidationErrorsException apiException = new ValidationErrorsException(errors);

    return buildExceptionResponse(apiException, baseException);
  }

  @ExceptionHandler({ DuplicateKeyException.class })
  @ResponseBody
  public ResponseEntity<ExceptionResponse> onDuplicateKeyException(DuplicateKeyException baseException) {
    String errorMessage = baseException.getMessage();

    // product-service.products index: slug dup key:
    Pattern pattern = Pattern.compile("index: (.*?) dup key:");
    Matcher matcher = pattern.matcher(errorMessage);

    String key = null;
    if (matcher.find()) {
      key = matcher.group(1);
    }

    if (key == null || key.isEmpty()) {
      log.error("Couldn't extract the key value from DuplicateKeyException", baseException);

      InternalServerErrorException apiException = new InternalServerErrorException();

      return buildExceptionResponse(apiException, baseException);
    }

    ValidationErrorsMap errors = new ValidationErrorsMap();
    errors.put(key, "must be unique");

    ValidationErrorsException apiException = new ValidationErrorsException(errors);

    return buildExceptionResponse(apiException, baseException);
  }

  @ExceptionHandler({ Exception.class })
  @ResponseBody
  public ResponseEntity<ExceptionResponse> onException(Exception baseException) {
    log.error("Uncaught exception", baseException);

    InternalServerErrorException apiException = new InternalServerErrorException();

    return buildExceptionResponse(apiException, baseException);
  }

  private ResponseEntity<ExceptionResponse> buildExceptionResponse(ApiException apiException,
      Exception baseException) {
    ExceptionResponse response = mapStructMapper.apiExceptionToExceptionResponse(apiException);

    if (returnStackTrace) {
      response.setStackTrace(baseException);
    }

    return ResponseEntity.status(response.getStatus()).body(response);
  }

}
package com.microservices.spring.apigatewayservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.microservices.spring.common.exceptions.ApiException;
import com.microservices.spring.common.responses.ExceptionResponse;

import reactor.core.publisher.Mono;

// https://github.com/anicetkeric/spring-webflux-handling-execptions/blob/main/src/main/java/com/example/springwebfluxhandlingexecptions/exception/GlobalErrorWebExceptionHandler.java
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

  public final static String CODE = "GATEWAY_EXCEPTION";

  @Value("${responses.exceptions.return-stack-trace:false}")
  private Boolean returnStackTrace;

  public GlobalErrorWebExceptionHandler(DefaultErrorAttributes defaultErrorAttributes,
      ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(defaultErrorAttributes, new WebProperties.Resources(),
        applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Throwable error = getError(request);

    HttpStatus status = determineHttpStatus(error);
    String code = determineCode(error);
    String message = determineMessage(error);

    if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
      message = "Internal server error";
    }

    ExceptionResponse response = ExceptionResponse.builder()
        .status(status.value())
        .code(code)
        .message(message)
        .build();

    if (returnStackTrace) {
      response.setStackTrace(error);
    }

    return ServerResponse.status(response.getStatus())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(response);
  }

  private HttpStatus determineHttpStatus(Throwable error) {
    HttpStatus status = null;

    if (error instanceof ResponseStatusException err) {
      status = HttpStatus.valueOf(err.getStatusCode().value());
    } else if (error instanceof ApiException err) {
      status = HttpStatus.valueOf(err.getStatus());
    } else {
      status = MergedAnnotations.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
          .get(ResponseStatus.class).getValue("code", HttpStatus.class)
          .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return status;
  }

  private String determineCode(Throwable error) {
    String code = CODE;

    if (error instanceof ApiException err) {
      code = err.getCode();
    }

    return code;
  }

  private String determineMessage(Throwable error) {
    String message = error.getMessage();

    if (error instanceof ResponseStatusException err) {
      message = err.getReason();
      if (message == null || message.isEmpty()) {
        message = err.getBody().getTitle();
      }
    }

    return message;
  }

}

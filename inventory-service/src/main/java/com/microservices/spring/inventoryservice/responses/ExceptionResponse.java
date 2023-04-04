package com.microservices.spring.inventoryservice.responses;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservices.spring.inventoryservice.exceptions.ApiException.ValidationErrorsMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {

  private int status;
  private String code;
  private String message;
  private ValidationErrorsMap errors;
  private Exception stackTrace;

  public String getStackTrace() {
    if (this.stackTrace == null) {
      return null;
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    this.stackTrace.printStackTrace(pw);
    return sw.toString();
  }

}
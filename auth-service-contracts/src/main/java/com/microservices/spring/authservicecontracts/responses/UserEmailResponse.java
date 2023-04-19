package com.microservices.spring.authservicecontracts.responses;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserEmailResponse {

  private UUID id;
  private String email;

}

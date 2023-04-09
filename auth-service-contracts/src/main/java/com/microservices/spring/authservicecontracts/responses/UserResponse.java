package com.microservices.spring.authservicecontracts.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {

  private UUID id;
  private String fullName;
  private String email;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}

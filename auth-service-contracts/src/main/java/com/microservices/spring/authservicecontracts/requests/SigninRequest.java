package com.microservices.spring.authservicecontracts.requests;

import java.util.Optional;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SigninRequest {

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Length(min = 6, message = "length must be greater or equals to 6")
  private String password;

  private Boolean rememberMe;

  public Boolean getRememberMe() {
    return Optional.ofNullable(rememberMe).orElse(false);
  }

}

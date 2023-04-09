package com.microservices.spring.authservicecontracts.requests;

import org.hibernate.validator.constraints.Length;

import com.microservices.spring.authservicecontracts.validators.IHasPasswordAndConfirmation;
import com.microservices.spring.authservicecontracts.validators.PasswordMatch;

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
@PasswordMatch
public class SignupRequest implements IHasPasswordAndConfirmation {

  @NotBlank
  private String fullName;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Length(min = 6, message = "length must be greater or equals to 6")
  private String password;

  @NotBlank
  @Length(min = 6, message = "length must be greater or equals to 6")
  private String passwordConfirmation;

}

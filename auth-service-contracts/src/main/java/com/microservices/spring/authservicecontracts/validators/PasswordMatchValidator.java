package com.microservices.spring.authservicecontracts.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, IHasPasswordAndConfirmation> {

  @Override
  public boolean isValid(IHasPasswordAndConfirmation value, ConstraintValidatorContext context) {
    if (value.getPassword() == null && value.getPasswordConfirmation() == null) {
      return true;
    } else if (value.getPassword() == null || value.getPasswordConfirmation() == null) {
      return false;
    }
    return value.getPassword().equals(value.getPasswordConfirmation());
  }

}
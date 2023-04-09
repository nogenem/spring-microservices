package com.microservices.spring.authservicecontracts.validators;

public interface IHasPasswordAndConfirmation {

  public String getPassword();

  public String getPasswordConfirmation();

}
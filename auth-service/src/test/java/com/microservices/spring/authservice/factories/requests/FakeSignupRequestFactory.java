package com.microservices.spring.authservice.factories.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.authservicecontracts.requests.SignupRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeSignupRequestFactory {

  private final Faker faker;

  public SignupRequest createOne() {
    String password = faker.internet().password(6, 10);
    return SignupRequest.builder()
        .fullName(faker.name().fullName())
        .email(faker.internet().emailAddress())
        .password(password)
        .passwordConfirmation(password)
        .build();
  }

  public List<SignupRequest> createMany(int length) {
    List<SignupRequest> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

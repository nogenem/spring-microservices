package com.microservices.spring.authservice.factories.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.authservicecontracts.requests.SigninRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeSigninRequestFactory {

  private final Faker faker;

  public SigninRequest createOne() {
    return SigninRequest.builder()
        .email(faker.internet().emailAddress())
        .password(faker.internet().password(6, 10))
        .build();
  }

  public List<SigninRequest> createMany(int length) {
    List<SigninRequest> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

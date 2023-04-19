package com.microservices.spring.notificationservice.factories.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.authservicecontracts.responses.UserEmailResponse;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeUserEmailResponseFactory {

  private final Faker faker;

  public UserEmailResponse createOne() {
    return UserEmailResponse.builder()
        .id(UUID.randomUUID())
        .email(faker.internet().emailAddress())
        .build();
  }

  public List<UserEmailResponse> createMany(int length) {
    List<UserEmailResponse> requests = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      requests.add(createOne());
    }

    return requests;
  }

}

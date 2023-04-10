package com.microservices.spring.authservice.factories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.microservices.spring.authservice.models.User;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FakeUserFactory {

  private final Faker faker;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public User createOne(String password) {
    return User.builder()
        .fullName(faker.name().fullName())
        .email(faker.internet().emailAddress())
        .passwordHash(bCryptPasswordEncoder.encode(password))
        .build();
  }

  public List<User> createMany(int length, String password) {
    List<User> users = new ArrayList<>();

    for (int i = 0; i < length; i++) {
      users.add(createOne(password));
    }

    return users;
  }

}

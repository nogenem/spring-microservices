package com.microservices.spring.authservice.integration.auth.users;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.authservice.BaseIntegrationTest;
import com.microservices.spring.authservice.UserRepository;
import com.microservices.spring.authservice.exceptions.UserWithThisIdNotFoundException;
import com.microservices.spring.authservice.factories.FakeUserFactory;
import com.microservices.spring.authservice.models.User;
import com.microservices.spring.common.exceptions.InvalidUserIdException;

public class FindUserEmailByIdTest extends BaseIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FakeUserFactory userFactory;

  @Test
  @DisplayName("Should be able to get users' emails by users' id")
  public void shouldBeAbleToGetUsersEmailsByUserId() throws JsonProcessingException, Exception {
    User savedUser = userFactory.createOne("password");
    userRepository.save(savedUser);

    String userId = savedUser.getId().toString();
    ResultActions resultActions = mvc.perform(get("/api/auth/users/{user_id}/email", userId)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedUser.getId().toString()))
        .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
  }

  @Test
  @DisplayName("Should not be able to get users' emails with an invalid user_id")
  public void shouldNotBeAbleToGetUsersWithAnInvalidUserId() throws JsonProcessingException, Exception {
    User savedUser = userFactory.createOne("password");
    userRepository.save(savedUser);

    // Invalid UUID
    String userId = savedUser.getId() + "_invalid";
    ResultActions resultActions = mvc.perform(get("/api/auth/users/{user_id}/email", userId)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(InvalidUserIdException.CODE));

    // UserId not found
    userId = UUID.randomUUID().toString();
    resultActions = mvc.perform(get("/api/auth/users/{user_id}/email", userId)
        .contentType(MediaType.APPLICATION_JSON));

    resultActions
        // .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(UserWithThisIdNotFoundException.CODE));
  }

}

package com.microservices.spring.authservice.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.authservice.BaseIntegrationTest;
import com.microservices.spring.authservice.UserRepository;
import com.microservices.spring.authservice.factories.FakeUserFactory;
import com.microservices.spring.authservice.models.User;
import com.microservices.spring.authservicecontracts.requests.SigninRequest;
import com.microservices.spring.authservicecontracts.responses.TokenResponse;
import com.microservices.spring.common.JwtTokenService;
import com.microservices.spring.common.exceptions.ValidationErrorsException;

public class SigninTest extends BaseIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FakeUserFactory userFactory;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  @DisplayName("Should be able to sign in")
  public void shouldBeAbleToSignIn() throws JsonProcessingException, Exception {
    String password = "password";

    User savedUser = userFactory.createOne(password);
    userRepository.save(savedUser);

    SigninRequest request = SigninRequest.builder()
        .email(savedUser.getEmail())
        .password(password)
        .build();

    ResultActions resultActions = mvc.perform(post("/api/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  @DisplayName("Should be able to control the token expiration using the `rememberMe` field")
  public void shouldBeAbleToControlTheTokenExpirationUsingTheRememberMeField()
      throws JsonProcessingException, Exception {
    String password = "password";

    User savedUser = userFactory.createOne(password);
    userRepository.save(savedUser);

    // RememberMe = false -> Token has expiration date
    SigninRequest request = SigninRequest.builder()
        .email(savedUser.getEmail())
        .password(password)
        .rememberMe(false)
        .build();

    ResultActions resultActions = mvc.perform(post("/api/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());

    String content = resultActions.andReturn().getResponse().getContentAsString();
    TokenResponse response = objectMapper.readValue(content, TokenResponse.class);

    Optional<Date> expiresAt = jwtTokenService.getExpiresAtFromToken(response.getToken());
    Assertions.assertTrue(expiresAt.isPresent());

    // RememberMe = true -> Token does not have expiration date
    request = SigninRequest.builder()
        .email(savedUser.getEmail())
        .password(password)
        .rememberMe(true)
        .build();

    resultActions = mvc.perform(post("/api/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());

    content = resultActions.andReturn().getResponse().getContentAsString();
    response = objectMapper.readValue(content, TokenResponse.class);

    expiresAt = jwtTokenService.getExpiresAtFromToken(response.getToken());
    Assertions.assertFalse(expiresAt.isPresent());
  }

  @Test
  @DisplayName("Should not be able to sign in with a not registered email")
  public void shouldNotBeAbleToSignInWithANotRegisteredEmail() throws JsonProcessingException, Exception {
    String password = "password";

    User savedUser = userFactory.createOne(password);
    userRepository.save(savedUser);

    SigninRequest request = SigninRequest.builder()
        .email(savedUser.getEmail() + ".net")
        .password(password)
        .build();

    ResultActions resultActions = mvc.perform(post("/api/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE));
  }

  @Test
  @DisplayName("Should not be able to sign in with the wrong password")
  public void shouldNotBeAbleToSignInWithTheWrongPassword() throws JsonProcessingException, Exception {
    String password = "password";

    User savedUser = userFactory.createOne(password);
    userRepository.save(savedUser);

    SigninRequest request = SigninRequest.builder()
        .email(savedUser.getEmail())
        .password(password + "_invalid")
        .build();

    ResultActions resultActions = mvc.perform(post("/api/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE));
  }

  @Test
  @DisplayName("Should not be able to sign in with info that dont comply by the validations")
  public void shouldNotBeAbleToSignInWithInfoThatDontComplyByTheValidations()
      throws JsonProcessingException, Exception {
    SigninRequest request = SigninRequest.builder()
        .email("fake@@email.com")
        .password("pa")
        .build();

    ResultActions resultActions = mvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE))
        .andExpect(jsonPath("$.errors").isMap())
        .andExpect(jsonPath("$.errors.email").isNotEmpty())
        .andExpect(jsonPath("$.errors.password").isNotEmpty());
  }

}

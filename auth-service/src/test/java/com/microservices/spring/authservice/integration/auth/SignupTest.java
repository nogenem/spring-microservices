package com.microservices.spring.authservice.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.microservices.spring.authservice.factories.requests.FakeSignupRequestFactory;
import com.microservices.spring.authservice.models.User;
import com.microservices.spring.authservicecontracts.requests.SignupRequest;
import com.microservices.spring.common.exceptions.ValidationErrorsException;

public class SignupTest extends BaseIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FakeUserFactory userFactory;

  @Autowired
  private FakeSignupRequestFactory signupRequestFactory;

  @Test
  @DisplayName("Should be able to sign up new users")
  public void shouldBeAbleToSignUpNewUsers() throws JsonProcessingException, Exception {
    SignupRequest request = signupRequestFactory.createOne();

    ResultActions resultActions = mvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value(request.getEmail()))
        .andExpect(jsonPath("$.id").isNotEmpty());

    Assertions.assertEquals(1, userRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to sign up with a non unique email")
  public void shouldNotBeAbleToSignUpWithANonUniqueEmail() throws JsonProcessingException, Exception {
    String email = "fake@email.com";

    User savedUser = userFactory.createOne("password");
    savedUser.setEmail(email);
    userRepository.save(savedUser);

    SignupRequest request = signupRequestFactory.createOne();
    request.setEmail(email);

    ResultActions resultActions = mvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    resultActions
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value(ValidationErrorsException.CODE));

    Assertions.assertEquals(1, userRepository.findAll().size());
  }

  @Test
  @DisplayName("Should not be able to sign up with info that dont comply by the validations")
  public void shouldNotBeAbleToSignUpWithInfoThatDontComplyByTheValidations()
      throws JsonProcessingException, Exception {
    SignupRequest request = SignupRequest.builder()
        .fullName("Na")
        .email("fake@@email.com")
        .password("pa")
        .passwordConfirmation("pas")
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
        .andExpect(jsonPath("$.errors.password").isNotEmpty())
        .andExpect(jsonPath("$.errors.passwordConfirmation").isNotEmpty())
        .andExpect(jsonPath("$.errors.global").isNotEmpty());

    Assertions.assertEquals(0, userRepository.findAll().size());
  }

}

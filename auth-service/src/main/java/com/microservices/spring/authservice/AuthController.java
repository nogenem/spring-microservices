package com.microservices.spring.authservice;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.authservice.models.User;
import com.microservices.spring.authservicecontracts.requests.SigninRequest;
import com.microservices.spring.authservicecontracts.requests.SignupRequest;
import com.microservices.spring.authservicecontracts.responses.TokenResponse;
import com.microservices.spring.authservicecontracts.responses.UserEmailResponse;
import com.microservices.spring.authservicecontracts.responses.UserResponse;
import com.microservices.spring.common.exceptions.InvalidUserIdException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Validated
@Tag(name = "Auth API")
public class AuthController {

  private final AuthService authService;
  private final MapStructMapper mapStructMapper;

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Sign up a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User signed up"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public UserResponse signup(@Valid @RequestBody SignupRequest request) {
    User user = authService.signup(request);

    return mapStructMapper.userToUserResponse(user);
  }

  @PostMapping("/signin")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Sign an user in")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Token for signed in user"),
      @ApiResponse(responseCode = "422", description = "Validation errors", content = @Content) })
  public TokenResponse signin(@Valid @RequestBody SigninRequest request) {
    String token = authService.signin(request);

    return new TokenResponse(token);
  }

  @GetMapping("/users/{user_id}/email")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get user email by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The user email found"),
      @ApiResponse(responseCode = "400", description = "Invalid user_id", content = @Content),
      @ApiResponse(responseCode = "404", description = "User with this id wasn't found", content = @Content) })
  public UserEmailResponse findUserEmailByUserId(@PathVariable("user_id") String userId) {
    UUID userIdUuid = userIdStringToUuid(userId);
    User user = authService.findById(userIdUuid);

    return mapStructMapper.userToUserEmailResponse(user);
  }

  private UUID userIdStringToUuid(String userId) {
    try {
      return UUID.fromString(userId);
    } catch (IllegalArgumentException exception) {
      throw new InvalidUserIdException(userId);
    }
  }

}

package com.microservices.spring.authservice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.spring.authservice.models.User;
import com.microservices.spring.authservicecontracts.requests.SignupRequest;
import com.microservices.spring.authservicecontracts.responses.UserResponse;

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

}

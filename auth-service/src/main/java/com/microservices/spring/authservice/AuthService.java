package com.microservices.spring.authservice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservices.spring.authservice.exceptions.InvalidCredentialsException;
import com.microservices.spring.authservice.models.User;
import com.microservices.spring.authservicecontracts.requests.SigninRequest;
import com.microservices.spring.authservicecontracts.requests.SignupRequest;
import com.microservices.spring.common.JwtTokenService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final MapStructMapper mapStructMapper;
  private final JwtTokenService tokenService;

  public User signup(SignupRequest request) {
    User user = mapStructMapper.signupRequestToUser(request);
    user.setPasswordHash(bCryptPasswordEncoder.encode(request.getPassword()));

    userRepository.save(user);

    log.info("User signed up. Id: {} - Email: {}", user.getId(), user.getEmail());

    return user;
  }

  public String signin(@Valid SigninRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new InvalidCredentialsException());

    if (!isValidPassword(user.getPasswordHash(), request.getPassword())) {
      throw new InvalidCredentialsException();
    }

    log.info("User signed in. Id: {} - Email: {} - RememberMe: {}", user.getId(), user.getEmail(),
        request.getRememberMe());

    return tokenService.generateToken(user.getId().toString(), !request.getRememberMe());
  }

  private boolean isValidPassword(String userPassword, String requestPassword) {
    return this.bCryptPasswordEncoder.matches(requestPassword, userPassword);
  }

}

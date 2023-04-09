package com.microservices.spring.authservice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservices.spring.authservice.models.User;
import com.microservices.spring.authservicecontracts.requests.SignupRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final MapStructMapper mapStructMapper;

  public User signup(SignupRequest request) {
    User user = mapStructMapper.signupRequestToUser(request);
    user.setPasswordHash(bCryptPasswordEncoder.encode(request.getPassword()));

    userRepository.save(user);

    log.info("User saved. Id: {} - Email: {}", user.getId(), user.getEmail());

    return user;
  }

}

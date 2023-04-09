package com.microservices.spring.authservice;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservices.spring.authservice.models.User;
import com.microservices.spring.authservicecontracts.requests.SignupRequest;
import com.microservices.spring.authservicecontracts.responses.UserResponse;

@Mapper(componentModel = "spring")
public interface MapStructMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User signupRequestToUser(SignupRequest signupRequest);

  UserResponse userToUserResponse(User user);

}

package com.microservices.spring.notificationservice.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.spring.authservicecontracts.responses.UserEmailResponse;

@FeignClient(name = "${feign-client.auth-service.url}/api/auth", contextId = "auth-service")
public interface AuthServiceClient {

  @GetMapping("/users/{user_id}/email")
  UserEmailResponse findUserEmailByUserId(@PathVariable("user_id") UUID userId);

}

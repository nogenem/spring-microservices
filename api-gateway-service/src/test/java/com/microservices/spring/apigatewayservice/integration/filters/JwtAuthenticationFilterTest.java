package com.microservices.spring.apigatewayservice.integration.filters;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.spring.apigatewayservice.BaseIntegrationTest;
import com.microservices.spring.apigatewayservice.exceptions.InvalidOrExpiredTokenException;
import com.microservices.spring.common.JwtTokenService;

public class JwtAuthenticationFilterTest extends BaseIntegrationTest {

  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  @DisplayName("Should be able to access public routes without a token")
  public void shouldBeAbleToAccessPublicRoutesWithoutAToken() throws JsonProcessingException {
    String url = "/api/auth/signup";
    String response = "{\"hello\": \"world\"}";

    stubFor(post(urlEqualTo(url))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
            .withBody(response)));

    ResponseSpec spec = client.post().uri(url).exchange();

    spec
        .expectStatus().isOk()
        .expectBody().json(response);
  }

  @Test
  @DisplayName("Should be able to access protected routes with a valid token")
  public void shouldBeAbleToAccessProtectedRoutesWithAValidToken() throws JsonProcessingException {
    String url = "/api/inventories";
    String response = "{\"hello\": \"world\"}";

    stubFor(post(urlEqualTo(url))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
            .withBody(response)));

    String userId = "some-user-id";
    String token = jwtTokenService.generateToken(userId, false);

    ResponseSpec spec = client.post().uri(url).header("Authorization", "Bearer " + token).exchange();

    spec
        .expectStatus().isOk()
        .expectBody().json(response);
  }

  @Test
  @DisplayName("Should not be able to access protected routes without an Authorization header")
  public void shouldNotBeAbleToAccessProtectedRoutesWithoutAnAuthorizationHeader() throws JsonProcessingException {
    String url = "/api/inventories";
    String response = "{\"hello\": \"world\"}";

    stubFor(get(urlEqualTo(url))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
            .withBody(response)));

    ResponseSpec spec = client.get().uri(url).exchange();

    spec.expectStatus().isUnauthorized();
  }

  @Test
  @DisplayName("Should not be able to access protected routes with an invalid Authorization header")
  public void shouldNotBeAbleToAccessProtectedRoutesWithAnInvalidAuthorizationHeader() throws JsonProcessingException {
    String url = "/api/inventories";
    String response = "{\"hello\": \"world\"}";

    stubFor(get(urlEqualTo(url))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
            .withBody(response)));

    // Empty header value
    ResponseSpec spec = client.get().uri(url).header("Authorization", "").exchange();

    spec
        .expectStatus().isBadRequest()
        .expectBody().jsonPath("$.code").isEqualTo(InvalidOrExpiredTokenException.CODE);

    // Empty Bearer value
    spec = client.get().uri(url).header("Authorization", "Bearer").exchange();

    spec
        .expectStatus().isBadRequest()
        .expectBody().jsonPath("$.code").isEqualTo(InvalidOrExpiredTokenException.CODE);

    // Invalid token value
    spec = client.get().uri(url).header("Authorization", "Bearer 123456789").exchange();

    spec
        .expectStatus().isBadRequest()
        .expectBody().jsonPath("$.code").isEqualTo(InvalidOrExpiredTokenException.CODE);
  }

}

package com.microservices.spring.common;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtTokenService {

  private static final String TOKEN_CLAIM_KEY = "id";

  @Value("${jwt-token.secret}")
  private String secret;

  @Value("${jwt-token.issuer}")
  private String issuer;

  @Value("${jwt-token.expires-in-minutes:120}")
  private Integer expiresInMinutes;

  public String generateToken(String userId, Boolean tokenExpires) {
    Algorithm algorithmHS = Algorithm.HMAC256(secret);
    Date now = new Date();

    JWTCreator.Builder builder = JWT.create()
        .withIssuer(issuer)
        .withClaim(TOKEN_CLAIM_KEY, userId)
        .withIssuedAt(now);

    if (tokenExpires) {
      int expiresInMS = expiresInMinutes * 60 * 1000;
      builder.withExpiresAt(new Date(now.getTime() + expiresInMS));
    }

    return builder.sign(algorithmHS);
  }

  public Boolean isValidToken(String token) {
    if (token == null) {
      return false;
    }

    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT.require(algorithm)
          .withIssuer(issuer)
          .build();

      verifier.verify(token);

      return true;
    } catch (JWTVerificationException exception) {
      log.info("Error while trying to validate token: " + exception.getMessage());
      return false;
    }
  }

  public Optional<String> getUserIdFromToken(String token) {
    if (token == null || token.isEmpty()) {
      return Optional.empty();
    }

    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT.require(algorithm)
          .withIssuer(issuer)
          .build();
      DecodedJWT jwt = verifier.verify(token);

      return Optional.ofNullable(jwt.getClaim(TOKEN_CLAIM_KEY).asString());
    } catch (JWTVerificationException exception) {
      log.info("Error while trying to extract `" + TOKEN_CLAIM_KEY + "` from token: " + exception.getMessage());
      return Optional.empty();
    }
  }

  public Optional<Date> getExpiresAtFromToken(String token) {
    if (token == null || token.isEmpty()) {
      return Optional.empty();
    }

    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT.require(algorithm)
          .withIssuer(issuer)
          .build();
      DecodedJWT jwt = verifier.verify(token);

      return Optional.ofNullable(jwt.getExpiresAt());
    } catch (JWTVerificationException exception) {
      log.info("Error while trying to extract `expiresAt` from token: " + exception.getMessage());
      return Optional.empty();
    }
  }

}

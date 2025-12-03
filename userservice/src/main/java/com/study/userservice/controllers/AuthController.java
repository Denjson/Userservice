package com.study.userservice.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@RestController
@RequestMapping(path = "/api/v1")
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  @GetMapping("/auth")
  public ResponseEntity<String> accessSecureResource(
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = null;
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      token = authorizationHeader.substring(7);
    }

    DecodedJWT decodedJWT = JWT.decode(token);
    Map<String, Claim> claims = decodedJWT.getClaims();
    log.info("___Token: {}", token);
    log.info("___Claims: {}", claims);
    log.info("___Id: {}", claims.get("UserId"));

    long epochMilli = 1000 * claims.get("exp").asLong(); // December 1, 2023, 00:00:00 UTC
    Instant instant = Instant.ofEpochMilli(epochMilli);
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTimeExp = instant.atZone(zoneId).toLocalDateTime();
    LocalDateTime localDateTimeNow = LocalDateTime.now();
    boolean exp = localDateTimeExp.isBefore(localDateTimeNow);
    String expired = "Token expired: " + exp;
    log.info(expired);
    if (token != null) {
      //      userService.addThreeTestUsers();
      return ResponseEntity.ok(
          "Access token information:\n" + token + "\n" + "Claims: " + claims + "\n" + expired);

    } else {
      return ResponseEntity.status(401).body("Unauthorized: Token missing or invalid.");
    }
  }
}

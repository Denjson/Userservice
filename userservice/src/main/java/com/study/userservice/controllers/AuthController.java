package com.study.userservice.controllers;

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
import com.study.userservice.auth.JwtService;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.interfaces.UserService;

@RestController
@RequestMapping(path = "/api/v1")
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  JwtService jwtService;
  UserService userService;
  UserRepository userRepository;

  public AuthController(JwtService jwtService, UserService userService) {
    super();
    this.jwtService = jwtService;
    this.userService = userService;
  }

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

    if (token != null) {
      //      userService.addThreeTestUsers();
      return ResponseEntity.ok("Access granted with token:\n" + token + "\n" + "Claims: " + claims);

    } else {
      return ResponseEntity.status(401).body("Unauthorized: Token missing or invalid.");
    }
  }
}

package com.study.userservice.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicatedValueException.class)
  public ResponseEntity<Map<String, Object>> handleDuplicatedValueException(
      DuplicatedValueException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.CONFLICT.value());
    errorDetails.put("error", "Duplicate value detected");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.CONFLICT.value());
    errorDetails.put("error", "Already Existing");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(IdNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUserNotFoundException(IdNotFoundException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.NOT_FOUND.value());
    errorDetails.put("error", "Not Found");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidUserCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidUserCredentialsException(
      InvalidUserCredentialsException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
    errorDetails.put("error", "Bad Request");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(
      UserAlreadyExistsException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.CONFLICT.value());
    errorDetails.put("error", "User Already Existing");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(DateValidationException.class)
  public ResponseEntity<Map<String, Object>> handleDateValidationException(
      DateValidationException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
    errorDetails.put("error", "Age limitation");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
  }

  @ExceptionHandler(NotValidValueException.class)
  public ResponseEntity<Map<String, Object>> handleNotValidValueException(
      NotValidValueException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.EXPECTATION_FAILED.value());
    errorDetails.put("error", "Null is not allowed");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.EXPECTATION_FAILED);
  }

  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<Map<String, Object>> handleTokenExpiredException(TokenExpiredException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.NOT_ACCEPTABLE.value());
    errorDetails.put("error", "Token is already expired");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_ACCEPTABLE);
  }

  @ExceptionHandler(CardQuantityLimitException.class)
  public ResponseEntity<Map<String, Object>> handleCardQuantityLimitException(
      CardQuantityLimitException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("timestamp", LocalDateTime.now());
    errorDetails.put("status", HttpStatus.CONFLICT.value());
    errorDetails.put("error", "Quantity limit achieved");
    errorDetails.put("message", ex.getMessage());
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }
}

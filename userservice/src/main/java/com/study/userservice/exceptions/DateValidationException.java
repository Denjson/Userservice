package com.study.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class DateValidationException extends RuntimeException {

  private static final long serialVersionUID = 5841307753904006377L;

  public DateValidationException(String message) {
    super(message);
  }
}

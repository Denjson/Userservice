package com.study.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class NotValidValueException extends RuntimeException {

  private static final long serialVersionUID = 836907646251522329L;

  public NotValidValueException(String message) {
    super(message);
  }
}

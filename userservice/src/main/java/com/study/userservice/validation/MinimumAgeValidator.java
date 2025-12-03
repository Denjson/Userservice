package com.study.userservice.validation;

import java.time.LocalDateTime;

import com.study.userservice.exceptions.DateValidationException;
import com.study.userservice.exceptions.NotValidValueException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinimumAgeValidator implements ConstraintValidator<MinimumAge, LocalDateTime> {

  private int minimumAge;

  @Override
  public void initialize(MinimumAge constraintAnnotation) {
    this.minimumAge = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(LocalDateTime birthdate, ConstraintValidatorContext context) {
    if (birthdate == null) {
      throw new NotValidValueException("Date of birth cannot be null");
    }
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime past = currentDate.minusYears(minimumAge);
    // when day of birthday is today, need to add comparing time with previous day
    int p = past.compareTo(birthdate);
    if (p < 0) {
      throw new DateValidationException("You must be at least " + minimumAge + " years old");
    }
    return p > 0;
  }
}

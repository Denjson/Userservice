package com.study.userservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO implements Serializable {

  private static final long serialVersionUID = 1463916941164267187L;

  private Long id;

  @NotEmpty(message = "Name can not be a null or empty")
  @Size(min = 3, max = 30, message = "The length of the customer name should be between 3 and 30")
  private String name;

  private String surname;

  private LocalDateTime birthDate;

  @NotEmpty(message = "Email address can not be a null or empty")
  @Email(message = "Email address should be a valid value")
  private String email;

  private boolean active;
}

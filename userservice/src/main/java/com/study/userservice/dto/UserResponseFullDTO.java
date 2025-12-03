package com.study.userservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.study.userservice.auth.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseFullDTO implements Serializable {

  private static final long serialVersionUID = 5454001051129590743L;

  private Long id;

  private String name;

  private String surname;

  private LocalDateTime birthDate;

  private String email;

  private boolean active;

  private Role role;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}

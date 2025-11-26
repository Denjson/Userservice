package com.study.userservice.entity;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.study.userservice.auditing.UserEntityListener;
import com.study.userservice.auth.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@EntityListeners(UserEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "den_schema")
public class User implements UserDetails {

  private static final long serialVersionUID = 518584766514502119L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "surname")
  private String surname;

  @Column(name = "birth_date")
  private LocalDateTime birthDate;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "active")
  private boolean active;

  //  @Transient
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() { // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUsername() { // TODO Auto-generated method stub
    return email;
  }
}

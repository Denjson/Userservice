package com.study.userservice.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.study.userservice.audit.AuditableEntity;
import com.study.userservice.auth.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// @Data
@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "den_schema")
public class User extends AuditableEntity implements UserDetails {

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

  @Column(name = "role", nullable = true)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(active, birthDate, email, id, name, role, surname);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    User other = (User) obj;
    return active == other.active
        && Objects.equals(birthDate, other.birthDate)
        && Objects.equals(email, other.email)
        && Objects.equals(id, other.id)
        && Objects.equals(name, other.name)
        && role == other.role
        && Objects.equals(surname, other.surname);
  }
}

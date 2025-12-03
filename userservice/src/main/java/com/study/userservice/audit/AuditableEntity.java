package com.study.userservice.audit;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AuditableEntity {

  @Column(name = "created_at", nullable = false)
  @CreatedDate
  protected LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = true)
  @LastModifiedDate
  protected LocalDateTime updatedAt;

  //  @CreatedBy protected String createdBy;
  //  @LastModifiedBy protected String lastModifiedBy;
  //  public String getCreatedBy() {
  //    return createdBy;
  //  }
  //
  //  public void setCreatedBy(String createdBy) {
  //    this.createdBy = createdBy;
  //  }

  @PrePersist
  protected void onCreate() {
    setCreatedAt(LocalDateTime.now());
  }

  //  @PreUpdate	// Not working while updating User with PUT method
  //  protected void onUpdate() {
  //    setUpdatedAt(LocalDateTime.now());
  //  }

  //  @PreRemove
  //  protected void onRemove() {
  //    setUpdatedAt(LocalDateTime.now());
  //  }

  @Override
  public int hashCode() {
    return Objects.hash(createdAt, updatedAt);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AuditableEntity other = (AuditableEntity) obj;
    return Objects.equals(createdAt, other.createdAt) && Objects.equals(updatedAt, other.updatedAt);
  }
}

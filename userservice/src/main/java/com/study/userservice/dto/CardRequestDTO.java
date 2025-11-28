package com.study.userservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRequestDTO implements Serializable {

  private static final long serialVersionUID = 801202695987409258L;

  private Long id;

  @NotNull(message = "User ID can not be null")
  private Long userId;

  @Min(1000000000000000L)
  @Max(9999999999999999L)
  @NotNull(message = "Card can not be a null or empty")
  private Long number;

  @NotEmpty(message = "Holder name can not be a null or empty")
  private String holder;

  private LocalDateTime expirationDate;

  private boolean active;
}

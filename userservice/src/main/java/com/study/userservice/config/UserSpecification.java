package com.study.userservice.config;

import org.springframework.data.jpa.domain.Specification;

import com.study.userservice.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserSpecification {

  public static Specification<User> nameContains(String keyword) {
    return (root, query, builder) ->
        builder.like(builder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
  }
}

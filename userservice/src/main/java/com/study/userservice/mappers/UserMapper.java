package com.study.userservice.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.study.userservice.auth.Role;
import com.study.userservice.dto.UserRequestDTO;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.entity.User;

@Component
public class UserMapper {

  public UserResponseDTO toDTO(User user) {
    if (user == null) {
      return null;
    }
    return new UserResponseDTO(
        user.getId(),
        user.getName(),
        user.getSurname(),
        user.getBirthDate(),
        user.getEmail(),
        user.isActive());
  }

  public List<UserResponseDTO> toDTOs(List<User> users) {
    return users.stream().map(this::toDTO).collect(Collectors.toList());
  }

  public User toEntity(UserRequestDTO userRequestDto) {
    if (userRequestDto == null) {
      return null;
    }
    User user = new User();
    user.setId(userRequestDto.getId());
    user.setName(userRequestDto.getName());
    user.setSurname(userRequestDto.getSurname());
    user.setBirthDate(userRequestDto.getBirthDate());
    user.setEmail(userRequestDto.getEmail());
    user.setActive(userRequestDto.isActive());
    user.setRole(Role.USER);
    return user;
  }

  public List<User> manyToEntity(List<UserRequestDTO> userDTOs) {
    return userDTOs.stream().map(this::toEntity).collect(Collectors.toList());
  }
}

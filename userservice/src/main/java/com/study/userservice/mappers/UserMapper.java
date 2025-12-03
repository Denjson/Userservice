package com.study.userservice.mappers;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.study.userservice.dto.UserRequestDTO;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.dto.UserResponseFullDTO;
import com.study.userservice.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  //  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  //  @Mapping(target = "role", ignore = true) // field "role" will be shown with null
  public UserResponseDTO toDTO(User user);

  public List<UserResponseDTO> toDTOs(List<User> users);

  @InheritInverseConfiguration
  public User toEntity(UserRequestDTO userRequestDto);

  @InheritInverseConfiguration
  public List<User> manyToEntity(List<UserRequestDTO> userDTOs);

  public List<UserResponseFullDTO> toFullDTOs(List<User> users);
}

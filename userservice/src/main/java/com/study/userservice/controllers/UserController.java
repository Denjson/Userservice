package com.study.userservice.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.userservice.dto.UserRequestDTO;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.entity.UserHistory;
import com.study.userservice.service.interfaces.UserService;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {

  @Autowired UserDetailsService details;
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(path = "/user")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {

    UserResponseDTO userResponseDTO = userService.saveOne(userRequestDTO);
    return ResponseEntity.ok(userResponseDTO);
  }

  @PostMapping(path = "/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponseDTO>> createUsers(
      @RequestBody List<UserRequestDTO> UserRequestDTOs) {
    List<UserResponseDTO> userResponseDTOs = userService.saveMany(UserRequestDTOs);
    return ResponseEntity.ok(userResponseDTOs);
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
    UserResponseDTO userResponseDTO = userService.getById(id);
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping("/users/{ids}")
  public ResponseEntity<List<UserResponseDTO>> getUsersByIds(@PathVariable Set<Long> ids) {
    List<UserResponseDTO> userResponseDTOs = userService.getByIds(ids);
    return ResponseEntity.ok(userResponseDTOs);
  }

  @GetMapping("/user/email/{email}")
  public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
    UserResponseDTO userResponseDTO = userService.getByEmail(email);
    return ResponseEntity.ok(userResponseDTO);
  }

  @PutMapping("/user/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> updateUser(
      @PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
    UserResponseDTO userResponseDTO = userService.updateUser(id, userRequestDTO);
    return ResponseEntity.ok(userResponseDTO);
  }

  @DeleteMapping("/user/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> deleteUserById(@PathVariable Long id) {
    UserResponseDTO userResponseDTO = userService.deleteById(id);
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping(path = "/admin/test")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponseDTO>> addTestUser() {
    List<UserResponseDTO> userResponseDTO = userService.addTestUser();
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping(path = "/admin/dellast")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> deleteLastUser() {
    UserResponseDTO userResponseDTO = userService.delUserLast();
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping(path = "/user/last")
  public ResponseEntity<UserResponseDTO> getLastUser() {
    UserResponseDTO userResponseDTO = userService.getUserLast();
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping(path = "/users/{page}/{itemsPerPage}")
  public ResponseEntity<List<UserResponseDTO>> getAllUsers(
      @PathVariable Integer page, @PathVariable Integer itemsPerPage) {
    return ResponseEntity.ok(userService.getAllUsers(page, itemsPerPage));
  }

  @GetMapping(path = "/user/random")
  public ResponseEntity<UserResponseDTO> getRandomUse() {
    UserResponseDTO userResponseDTO = userService.getRandomUser();
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping(path = "/user/native/{n}")
  public ResponseEntity<List<UserResponseDTO>> getRangeIds(@PathVariable Integer n) {
    List<UserResponseDTO> userResponseDTOs = userService.getRangeIds(n);
    return ResponseEntity.ok(userResponseDTOs);
  }

  @GetMapping(path = "/user/jpql/{lastname}")
  public ResponseEntity<List<UserResponseDTO>> findByJPQL(@PathVariable String lastname) {
    List<UserResponseDTO> userResponseDTOs = userService.findByJPQL(lastname);
    return ResponseEntity.ok(userResponseDTOs);
  }

  @GetMapping(path = "/user/log")
  public ResponseEntity<List<UserHistory>> getUsersLog() {
    return ResponseEntity.ok(userService.getUserLog());
  }

  @GetMapping("/user/active/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponseDTO> changeActiveStatus(@PathVariable Long id) {
    UserResponseDTO userResponseDTO = userService.changeActive(id);
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping("/user/name/{keyword}")
  public ResponseEntity<List<UserResponseDTO>> getUserById(@PathVariable String keyword) {
    List<UserResponseDTO> userResponseDTO = userService.getNamesContainsText(keyword);
    return ResponseEntity.ok(userResponseDTO);
  }
}

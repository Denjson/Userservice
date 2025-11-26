package com.study.userservice;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.study.userservice.auth.Role;
import com.study.userservice.dto.UserRequestDTO;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.entity.User;
import com.study.userservice.exceptions.IdNotFoundException;
import com.study.userservice.mappers.UserMapper;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.UserServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserServiceImpl userServiceImpl;

  @Spy private UserMapper userMapper;

  //  private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

  User existingUser;
  LocalDateTime stamp;
  UserRequestDTO userRequestDTO;

  @BeforeEach
  void setUp() {
    stamp = LocalDateTime.of(2025, 11, 25, 10, 30);
    existingUser = new User(1L, "John", "Connor", stamp, "john@mail.com", true, Role.USER);
    userRequestDTO = new UserRequestDTO(1L, "John", "Connor", stamp, "john@mail.com", true);
  }

  @Test
  void saveOneTest() {
    when(userRepository.save(existingUser)).thenReturn(existingUser);
    UserResponseDTO result = userServiceImpl.saveOne(userRequestDTO);
    log.info("___ Result: {}", result);
    assertNotNull(result);
    assertEquals(existingUser.getId(), result.getId());
    assertEquals("john@mail.com", result.getEmail());
  }

  @Test
  void saveManyTest() {
    List<User> users = new ArrayList<>();
    List<UserRequestDTO> dtos = new ArrayList<>();
    for (Long i = 0L; i < 2; i++) {
      User user = new User(i, "John", "Connor", stamp, i + "@mail.com", true, Role.USER);
      users.add(user);
      UserRequestDTO userRequestDTO =
          new UserRequestDTO(i, "John", "Connor", stamp, i + "@mail.com", true);
      dtos.add(userRequestDTO);
    }
    log.info("___ users: {}", users);
    log.info("___ dtos: {}", dtos);
    when(userRepository.saveAll(users)).thenReturn(users);
    List<UserResponseDTO> result = userServiceImpl.saveMany(dtos);
    log.info("___ Result: {}", result);
    assertEquals(users.size(), result.size());
    assertEquals(users.getFirst().getEmail(), result.getFirst().getEmail());
  }

  @Test
  void getByIdTest() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    UserResponseDTO result = userServiceImpl.getById(userId);
    log.info("___ Result: {}", result);
    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertEquals(existingUser.getName(), result.getName());
  }

  @Test
  void deleteByIdTest() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    assertThatCode(() -> userServiceImpl.deleteById(1L)).doesNotThrowAnyException();
    verify(userRepository).deleteById(1L);
  }

  @Test
  void deleteByIdwithNonexistingIdTest() {
    when(userRepository.findById(2L))
        .thenThrow(new IdNotFoundException("User not found with id: " + 2L));
    assertThatThrownBy(() -> userServiceImpl.deleteById(2L))
        .isInstanceOf(IdNotFoundException.class)
        .hasMessage("User not found with id: " + 2L);
    verify(userRepository, never()).deleteById(anyLong());
  }

  @Test
  void getByIdsTest() {
    Set<Long> ids = Set.of(0L, 1L);
    List<User> users = new ArrayList<>();
    for (Long i = 0L; i < 2; i++) {
      User user = new User(i, "John", "Connor", stamp, i + "@mail.com", true, Role.USER);
      users.add(user);
    }
    when(userRepository.findByIdIn(ids)).thenReturn(Optional.of(users));
    List<UserResponseDTO> result = userServiceImpl.getByIds(ids);
    log.info("___ Users: {}", users);
    log.info("___ Result: {}", result);
    assertEquals(users.size(), result.size());
    assertEquals(users.getFirst().getEmail(), result.getFirst().getEmail());
  }

  @Test
  void getByEmailTest() {
    String email = "john@mail.com";
    when(userRepository.getByEmail(email)).thenReturn(Optional.of(existingUser));
    UserResponseDTO result = userServiceImpl.getByEmail(email);
    log.info("___ Result: {}", result);
    log.info("___ Result ID: {}", result.getId());
    assertNotNull(result);
    assertEquals(existingUser.getEmail(), result.getEmail());
  }

  @Test
  void updateUserTest() {
    UserRequestDTO userRequestDTO =
        new UserRequestDTO(1L, "JohnNew", "Connor", stamp, "john@mail.com", true);
    when(userRepository.getByEmail(userRequestDTO.getEmail()))
        .thenReturn(Optional.of(existingUser));
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    User userUpdated = new User(1L, "JohnNew", "Connor", stamp, "john@mail.com", true, Role.USER);
    when(userRepository.save(userUpdated)).thenReturn(userUpdated);
    UserResponseDTO result = userServiceImpl.updateUser(1L, userRequestDTO);
    log.info("___ Result: {}", result);
    assertEquals("john@mail.com", result.getEmail());
    assertEquals("JohnNew", result.getName());
  }

  @Test
  void getAllUsersTest() {
    List<User> users = new ArrayList<>();
    for (Long i = 0L; i < 2; i++) {
      User user = new User(i, "John", "Connor", stamp, i + "@mail.com", true, Role.USER);
      users.add(user);
    }
    Pageable pageable = PageRequest.of(0, 10, Sort.by("Id"));
    Page<User> mockPage = new PageImpl<>(users, pageable, 0);
    when(userRepository.findAll(pageable)).thenReturn(mockPage);
    List<UserResponseDTO> result = userServiceImpl.getAllUsers(0, 10);
    log.info("___ Result: {}", result);
    assertEquals(users.size(), result.size());
    assertEquals(users.getFirst().getEmail(), result.getFirst().getEmail());
  }

  @Test
  void getUserLastTest() {
    when(userRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingUser));
    UserResponseDTO result = userServiceImpl.getUserLast();
    assertEquals(existingUser.getId(), result.getId());
  }

  @Test
  void delUserLastTest() {
    when(userRepository.count()).thenReturn(1L);
    when(userRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(existingUser));
    assertThatCode(() -> userServiceImpl.delUserLast()).doesNotThrowAnyException();
    verify(userRepository).delete(existingUser);
  }

  //  @Test
  void addTestUserTest() {
    //    userServiceImpl.addTestUser() is providing random e-mail.
  }

  @Test
  void getRandomUserTest() {
    List<User> users = new ArrayList<>();
    users.add(existingUser);
    when(userRepository.findAll()).thenReturn(users);
    UserResponseDTO result = userServiceImpl.getRandomUser();
    assertEquals(existingUser.getEmail(), result.getEmail());
  }

  @Test
  void getRangeIdsTest() {
    List<User> users = new ArrayList<>();
    users.add(existingUser);
    when(userRepository.findIdsNative(0)).thenReturn(users);
    List<UserResponseDTO> result = userServiceImpl.getRangeIds(0);
    assertEquals(existingUser.getEmail(), result.getFirst().getEmail());
  }

  @Test
  void findByJPQLTest() {
    List<User> users = new ArrayList<>();
    users.add(existingUser);
    when(userRepository.findByLastNameJPQL(existingUser.getSurname())).thenReturn(users);
    List<UserResponseDTO> result = userServiceImpl.findByJPQL(existingUser.getSurname());
    assertEquals(existingUser.getSurname(), result.getFirst().getSurname());
  }

  @Test
  void changeActiveTest() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    UserResponseDTO result = userServiceImpl.changeActive(1L);
    log.info("___ Is user active? {}", result);
    assertEquals(existingUser.isActive(), false);
  }

  @Test
  void getNamesContainsTextTest() {
    List<User> users = new ArrayList<>();
    users.add(existingUser);
    //    Specification<User> spec = UserSpecification.nameContains("keyword");
    //    when(userRepository.findAll(spec).thenReturn(users));
    when(userRepository.findAll(Mockito.<Specification<User>>any())).thenReturn(users);
    List<UserResponseDTO> result = userServiceImpl.getNamesContainsText("h");
    log.info("___ Name contains 'h': {}", result);
    assertEquals(existingUser.getSurname(), result.getFirst().getSurname());
  }
}

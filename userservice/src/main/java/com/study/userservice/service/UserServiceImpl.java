package com.study.userservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.study.userservice.auth.Role;
import com.study.userservice.config.UserSpecification;
import com.study.userservice.dto.UserRequestDTO;
import com.study.userservice.dto.UserResponseDTO;
import com.study.userservice.entity.User;
import com.study.userservice.entity.UserHistory;
import com.study.userservice.exceptions.DuplicatedValueException;
import com.study.userservice.exceptions.IdNotFoundException;
import com.study.userservice.mappers.UserMapper;
import com.study.userservice.repository.UserHistoryRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final UserHistoryRepository userHistoryRepository;

  public UserServiceImpl(
      UserRepository userRepository,
      UserMapper userMapper,
      UserHistoryRepository userHistoryRepository) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.userHistoryRepository = userHistoryRepository;
  }

  @CacheEvict(value = "all", allEntries = true)
  public UserResponseDTO saveOne(UserRequestDTO userRequestDTO) {
    if (userRepository.getByEmail(userRequestDTO.getEmail()).isPresent()) {
      throw new DuplicatedValueException(
          "User with e-mail is already existing: " + userRequestDTO.getEmail());
    }
    User user = userRepository.save(userMapper.toEntity(userRequestDTO));
    log.info("___Saved User: {}", userRequestDTO);
    return userMapper.toDTO(user);
  }

  @CacheEvict(value = "all", allEntries = true)
  public List<UserResponseDTO> saveMany(List<UserRequestDTO> userRequestDTOs) {
    List<String> emails =
        userRequestDTOs.stream().map(UserRequestDTO::getEmail).collect(Collectors.toList());
    //    Set<String> set = new HashSet<>(emails);
    //    userRepository.findByEmailIn(set);
    for (String email : emails) {
      if (userRepository.getByEmail(email).isPresent()) {
        throw new DuplicatedValueException("User with e-mail is already existing: " + email);
      }
    }
    List<User> manyUsers = userRepository.saveAll(userMapper.manyToEntity(userRequestDTOs));
    log.info("___Saved Users: {}", userRequestDTOs);
    return userMapper.toDTOs(manyUsers);
  }

  @Cacheable(value = "users", key = "#id")
  public UserResponseDTO getById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("User not found with id: " + id));
    return userMapper.toDTO(user);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "all", allEntries = true),
        @CacheEvict(value = "allcards", allEntries = true)
      })
  public UserResponseDTO deleteById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("User not found with id: " + id));
    userRepository.deleteById(id);
    return userMapper.toDTO(user);
  }

  public List<UserResponseDTO> getByIds(Set<Long> ids) {
    List<User> users = userRepository.findByIdIn(ids).get();
    if (users.isEmpty()) {
      throw new IdNotFoundException("Users not found with ids: " + ids);
    }
    log.info("___Users found: {}", users.toString());
    return userMapper.toDTOs(users);
  }

  public UserResponseDTO getByEmail(String email) {
    User user =
        userRepository
            .getByEmail(email)
            .orElseThrow(() -> new IdNotFoundException("Email not found: " + email));
    return userMapper.toDTO(user);
  }

  @CachePut(value = "users", key = "#id")
  @CacheEvict(value = "all", allEntries = true)
  public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("User not found with id: " + id));
    Optional<User> u = userRepository.getByEmail(userRequestDTO.getEmail());

    if (u.isPresent() && u.get().getId() != id) {
      throw new DuplicatedValueException(
          "User with e-mail is already existing: " + userRequestDTO.getEmail());
    }
    userRequestDTO.setId(id);
    user = userMapper.toEntity(userRequestDTO);
    userRepository.save(user);
    log.info("___User updated: {}", user.toString());
    return userMapper.toDTO(user);
  }

  @Cacheable(value = "all", key = "#page + '-' + #itemsPerPage")
  public List<UserResponseDTO> getAllUsers(Integer page, Integer itemsPerPage) {
    Pageable pageable = PageRequest.of(page, itemsPerPage, Sort.by("Id"));
    List<User> users = userRepository.findAll(pageable).getContent();
    if (users.isEmpty()) {
      throw new IdNotFoundException("List of users is empty");
    }
    log.info("___All users from UserService: {}", users.toString());
    List<UserResponseDTO> userResponseDTOs = userMapper.toDTOs(users);
    return userResponseDTOs;
  }

  public UserResponseDTO getUserLast() {
    User user =
        userRepository
            .findTopByOrderByIdDesc()
            .orElseThrow(() -> new IdNotFoundException("No any users available"));
    log.info("___Last id is: {}, {}", user.getId(), user);
    UserResponseDTO userResponseDTO = userMapper.toDTO(user);
    return userResponseDTO;
  }

  @CacheEvict(value = "all", allEntries = true)
  public UserResponseDTO delUserLast() {
    log.info("___userRepository.count(): {}", userRepository.count());
    if (userRepository.count() > 0) {
      User user = userRepository.findTopByOrderByIdDesc().get();
      log.info("___Delete last record: {}", user);
      UserResponseDTO userResponseDTO = userMapper.toDTO(user);
      userRepository.delete(user);
      return userResponseDTO;
    } else {
      throw new IdNotFoundException("Users list is empty");
    }
  }

  @CacheEvict(value = "all", allEntries = true)
  public List<UserResponseDTO> addTestUser() {
    log.info("___Quantity of users: {}", userRepository.count());
    User u = new User();
    u.setName("X CODE");
    u.setSurname("MANCODE");
    u.setBirthDate(LocalDateTime.now().withNano(0));
    u.setEmail((int) (Math.random() * 10000) + "@me.com");
    u.setActive(true);
    u.setRole(Role.ADMIN);
    userRepository.save(u);
    List<User> users = userRepository.findAll();
    return userMapper.toDTOs(users);
  }

  public UserResponseDTO getRandomUser() {
    List<User> entityList = new ArrayList<>();
    userRepository.findAll().forEach(entityList::add);
    if (entityList.isEmpty()) {
      throw new IdNotFoundException("List of users is empty");
    }
    Collections.shuffle(entityList);
    User user = entityList.getFirst();
    log.info("___Random user: {}", user.toString());
    return userMapper.toDTO(user);
  }

  public List<UserResponseDTO> getRangeIds(Integer n) {
    List<User> users = userRepository.findIdsNative(n);
    log.info("___All users within range: {}", users.toString());
    List<UserResponseDTO> userResponseDTOs = userMapper.toDTOs(users);
    return userResponseDTOs;
  }

  public List<UserResponseDTO> findByJPQL(String lastname) {
    List<User> users = userRepository.findByLastNameJPQL(lastname);
    if (users.isEmpty()) {
      throw new IdNotFoundException("Users not found with lastname: " + lastname);
    }
    log.info("___All users with name <{}> found: {}", lastname, users.toString());
    List<UserResponseDTO> userResponseDTOs = userMapper.toDTOs(users);
    return userResponseDTOs;
  }

  public List<UserHistory> getUserLog() {
    List<UserHistory> list = userHistoryRepository.findAll();
    log.info("___All users within range: {}", list.toString());
    return list;
  }

  public void addThreeTestUsers() {

    UserRequestDTO admin =
        UserRequestDTO.builder()
            .name("Admin")
            .surname("Admin")
            .birthDate(LocalDateTime.now())
            .email("admin@mail.com")
            .active(true)
            .build();
    log.info("___Admin details: {}", admin);
    if (userRepository.getByEmail("admin@mail.com").isEmpty()) {
      saveOne(admin);
    }

    UserRequestDTO manager =
        UserRequestDTO.builder()
            .name("Manager")
            .surname("Manager")
            .birthDate(LocalDateTime.now())
            .email("manager@mail.com")
            .active(true)
            .build();
    log.info("___Manager details: {}", manager);
    if (userRepository.getByEmail("manager@mail.com").isEmpty()) {
      saveOne(manager);
    }

    UserRequestDTO user =
        UserRequestDTO.builder()
            .name("User")
            .surname("User")
            .birthDate(LocalDateTime.now())
            .email("user@mail.com")
            .active(true)
            .build();
    log.info("___User details: {}", user);
    if (userRepository.getByEmail("user@mail.com").isEmpty()) {
      saveOne(user);
    }
  }

  @CacheEvict(value = "all", allEntries = true)
  public UserResponseDTO changeActive(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IdNotFoundException("User not found with id: " + id));
    user.setActive(!user.isActive());
    userRepository.save(user);
    log.info("___User updated: {}", user.toString());
    return userMapper.toDTO(user);
  }

  public List<UserResponseDTO> getNamesContainsText(String keyword) {
    Specification<User> spec = UserSpecification.nameContains(keyword);
    List<User> users = userRepository.findAll(spec);
    return userMapper.toDTOs(users);
  }
}

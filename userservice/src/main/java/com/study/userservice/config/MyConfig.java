package com.study.userservice.config;

// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AnonymousAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;

// import com.study.userservice.entity.User;
// import com.study.userservice.mappers.UserMapper;
// import com.study.userservice.repository.UserHistoryRepository;
import com.study.userservice.repository.UserRepository;

// import com.study.userservice.service.UserServiceImpl;

@Configuration
public class MyConfig {

  UserRepository userRepository;

  /**
   * Provides UserServiceImpl to use native queries with JpaRepository
   *
   * @return UserServiceImpl
   */
  //  @Bean
  //  public UserServiceImpl userService(
  //      UserRepository userRepository,
  //      UserMapper userMapper,
  //      UserHistoryRepository userHistoryRepository) {
  //    return new UserServiceImpl(userRepository, userMapper, userHistoryRepository);
  //  }

  //  @Bean
  //  public User getCurrentAuthenticatedUser() {
  //    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
  //    if (authentication != null
  //        && authentication.isAuthenticated()
  //        && !(authentication instanceof AnonymousAuthenticationToken)) {
  //      String userName = authentication.getName();
  //      System.out.println("_____getCurrentAuthenticatedUser" + userName);
  //      System.out.println(
  //          "_____getCurrentAuthenticatedUser" + userRepository.getByEmail(userName).get());
  //      return userRepository.getByEmail(userName).get();
  //    }
  //    return null; // Or handle anonymous user
  //  }
}

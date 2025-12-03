package com.study.userservice.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import org.springframework.security.authentication.AnonymousAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;

// @Configuration
public class MyConfig {

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

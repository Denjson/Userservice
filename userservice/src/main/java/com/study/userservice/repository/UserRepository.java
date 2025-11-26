package com.study.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.study.userservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  Optional<User> findTopByOrderByIdDesc();

  Optional<User> getByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<List<User>> findByIdIn(Set<Long> ids);

  Optional<List<User>> findByEmailIn(Set<String> email);

  @Query(value = "SELECT * FROM den_schema.users WHERE id > :n", nativeQuery = true)
  List<User> findIdsNative(Integer n);

  @Query("SELECT u FROM User u WHERE u.surname = :surname")
  List<User> findByLastNameJPQL(@Param("surname") String surname);
}

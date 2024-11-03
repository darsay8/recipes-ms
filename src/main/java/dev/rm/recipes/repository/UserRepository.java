package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  User findByEmail(String email);

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  User findByUserId(Long userId);
}

package dev.rm.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.recipes.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
  User findByUsername(String username);

  User findByEmail(String email);
}

package dev.rm.recipes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.User;
import dev.rm.recipes.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    if (users.isEmpty()) {
      log.info("No users found.");
      return ResponseEntity.noContent().build();
    } else {
      log.info("Returning {} users.", users.size());
      return ResponseEntity.ok(users);
    }
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    try {
      User user = userService.getUserById(id);
      log.info("Returning user with id {}", id);
      return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
      log.error("Error fetching user with id {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      log.info("Deleted user with id {}", id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      log.error("Error deleting user with id {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

}

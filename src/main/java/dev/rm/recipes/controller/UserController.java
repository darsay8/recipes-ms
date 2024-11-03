package dev.rm.recipes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.User;
import dev.rm.recipes.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    if (users.isEmpty()) {
      logger.info("No users found.");
      return ResponseEntity.noContent().build();
    } else {
      logger.info("Returning {} users.", users.size());
      return ResponseEntity.ok(users);
    }
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    try {
      User user = userService.getUserById(id);
      logger.info("Returning user with id {}", id);
      return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
      logger.error("Error fetching user with id {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      logger.info("Deleted user with id {}", id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      logger.error("Error deleting user with id {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

}

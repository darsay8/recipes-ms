package dev.rm.recipes.service;

import dev.rm.recipes.model.User;
import java.util.List;

public interface UserService {

  List<User> getAllUsers();

  User getUserById(Long userId);

  User createUser(User user);

  User updateUser(Long userId, User user);

  void deleteUser(Long userId);

  User getUserByEmail(String email);

}

package dev.rm.recipes.service;

import dev.rm.recipes.model.User;
import java.util.List;

public interface UserService {

  List<User> getAllUsers();

  User getUserById(Long userId);

  void deleteUser(Long userId);

}

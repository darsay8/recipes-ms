package dev.rm.recipes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.rm.recipes.exception.UserNotFoundException;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;

  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User not found with email " + email));
  }

  @Override
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException("User not found with id " + id);
    }
    userRepository.deleteById(id);
  }

}

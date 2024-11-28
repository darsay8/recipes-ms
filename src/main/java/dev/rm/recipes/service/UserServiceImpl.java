package dev.rm.recipes.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.rm.recipes.exception.UserNotFoundException;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;

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
  public User createUser(User user) {

    User newUser = User.builder()
        .username(user.getUsername())
        .password(passwordEncoder.encode(user.getPassword()))
        .email(user.getEmail())
        .role(Role.USER)
        .build();

    return userRepository.save(newUser);
  }

  @Override
  public User updateUser(Long id, User user) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

    existingUser.setUsername(user.getUsername());
    existingUser.setEmail(user.getEmail());
    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
    existingUser.setRole(user.getRole());
    return userRepository.save(existingUser);
  }

  @Override
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException("User not found with id " + id);
    }
    userRepository.deleteById(id);
  }

}

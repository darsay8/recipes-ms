package dev.rm.recipes.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.rm.recipes.exception.UserNotFoundException;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.UserRepository;

import java.util.List;
import java.util.Optional;

class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserServiceImpl(userRepository, passwordEncoder);
  }

  @Test
  void testGetAllUsers() {
    User user1 = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    User user2 = User.builder()
        .userId(2L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();
    when(userRepository.findAll()).thenReturn(List.of(user1, user2));

    List<User> users = userService.getAllUsers();

    assertNotNull(users);
    assertEquals(2, users.size());
    assertTrue(users.contains(user1));
    assertTrue(users.contains(user2));

    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testGetUserById() {
    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    User foundUser = userService.getUserById(1L);

    assertNotNull(foundUser);
    assertEquals(1L, foundUser.getUserId());
    assertEquals("user1", foundUser.getUsername());

    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void testGetUserById_NotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    assertEquals("User not found with id 1", exception.getMessage());

    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void testGetUserByEmail() {
    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();
    when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user));

    User foundUser = userService.getUserByEmail("user1@example.com");

    assertNotNull(foundUser);
    assertEquals("user1@example.com", foundUser.getEmail());
    assertEquals("user1", foundUser.getUsername());

    verify(userRepository, times(1)).findByEmail("user1@example.com");
  }

  @Test
  void testGetUserByEmail_NotFound() {
    when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        () -> userService.getUserByEmail("user1@example.com"));
    assertEquals("User not found with email user1@example.com", exception.getMessage());

    verify(userRepository, times(1)).findByEmail("user1@example.com");
  }

  @Test
  void testCreateUser() {
    User user = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    User newUser = User.builder()
        .userId(1L)
        .username("user1")
        .password("encodedPassword")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(newUser);

    User createdUser = userService.createUser(user);

    assertNotNull(createdUser);
    assertEquals("user1", createdUser.getUsername());
    assertEquals("user1@example.com", createdUser.getEmail());
    assertEquals("encodedPassword", createdUser.getPassword());
    assertEquals(Role.USER, createdUser.getRole());

    verify(userRepository, times(1)).save(any(User.class));
    verify(passwordEncoder, times(1)).encode("password");
  }

  @Test
  void testUpdateUser() {

    User existingUser = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("user1@example.com")
        .role(Role.USER)
        .build();

    User updatedUser = User.builder()
        .userId(1L)
        .username("updatedUser")
        .password("newPassword")
        .email("updatedUser@example.com")
        .role(Role.ADMIN)
        .build();

    when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    User updatedUserResult = userService.updateUser(1L, updatedUser);

    assertNotNull(updatedUserResult);
    assertEquals("updatedUser", updatedUserResult.getUsername());
    assertEquals("updatedUser@example.com", updatedUserResult.getEmail());
    assertEquals(Role.ADMIN, updatedUserResult.getRole());

    verify(userRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).save(any(User.class));
    verify(passwordEncoder, times(1)).encode("newPassword");
  }

  @Test
  void testUpdateUser_NotFound() {
    User updatedUser = User.builder()
        .userId(1L)
        .username("user1")
        .password("password")
        .email("userUpdated@example.com")
        .role(Role.ADMIN)
        .build();

    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        () -> userService.updateUser(1L, updatedUser));
    assertEquals("User not found with id 1", exception.getMessage());

    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void testDeleteUser() {
    when(userRepository.existsById(1L)).thenReturn(true);

    userService.deleteUser(1L);

    verify(userRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteUser_NotFound() {
    when(userRepository.existsById(1L)).thenReturn(false);

    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    assertEquals("User not found with id 1", exception.getMessage());

    verify(userRepository, times(1)).existsById(1L);
  }

}

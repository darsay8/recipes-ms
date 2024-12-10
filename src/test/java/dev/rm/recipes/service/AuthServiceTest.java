package dev.rm.recipes.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.rm.recipes.model.AuthResponse;
import dev.rm.recipes.model.LoginRequest;
import dev.rm.recipes.model.RegisterRequest;
import dev.rm.recipes.model.TokenValidationResponse;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.UserRepository;
import dev.rm.recipes.security.JwtTokenProvider;
import dev.rm.recipes.service.AuthService.AuthenticationException;
import dev.rm.recipes.service.AuthService.UserAlreadyExistsException;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthService authService;

  private LoginRequest loginRequest;
  private RegisterRequest registerRequest;
  private Authentication authentication;
  private String token;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    authService = new AuthService(authenticationManager, tokenProvider, userRepository, passwordEncoder);
    loginRequest = new LoginRequest("user@example.com", "password123");
    registerRequest = new RegisterRequest("user", " password123", "user@example.com");

    authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);

    token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzU3OTUyNywiZXhwIjoxNzMzNjY1OTI3fQ.g_IcKA8hcX6KZmCkzy7cwkjDw440dhob-cwTW0q3VMI";
    when(tokenProvider.generateToken(authentication)).thenReturn(token);
    when(tokenProvider.getUsernameFromToken(token)).thenReturn("user@example.com");
    when(tokenProvider.getRolesFromToken(token)).thenReturn("USER");
    when(tokenProvider.getEmailFromToken(token)).thenReturn("user@example.com");
  }

  @Test
  void testLoginSuccess() {

    AuthResponse response = authService.login(loginRequest);

    assertNotNull(response);
    assertEquals("user@example.com", response.getUsername());
    assertEquals("USER", response.getRole());
    assertEquals("user@example.com", response.getEmail());
    assertEquals(token, response.getToken());

  }

  @Test
  void testLoginFailure() {
    LoginRequest loginRequest = new LoginRequest("user@example.com", "wrong-password");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
      authService.login(loginRequest);
    });
    assertEquals("Invalid email or password", exception.getMessage());
  }

  @Test
  void testRegisterUsernameExists() {
    RegisterRequest registerRequest = new RegisterRequest("existinguser", "password123", "newuser@example.com");

    when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

    UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
      authService.register(registerRequest);
    });
    assertEquals("Username already exists", exception.getMessage());
  }

  @Test
  void testValidateTokenSuccess() {
    String token = "valid-jwt-token";

    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.getUsernameFromToken(token)).thenReturn("user@example.com");
    when(tokenProvider.getRolesFromToken(token)).thenReturn("USER");

    TokenValidationResponse response = authService.validateToken(token);

    assertTrue(response.isValid());
    assertEquals("user@example.com", response.getUsername());
    assertEquals("USER", response.getRoles());
  }

  @Test
  void testValidateTokenFailure() {
    String token = "invalid-jwt-token";

    when(tokenProvider.validateToken(token)).thenReturn(false);

    TokenValidationResponse response = authService.validateToken(token);

    assertFalse(response.isValid());
  }

  @Test
  void testLogout() {
    String authHeader = "Bearer fake-jwt-token";

    authService.logout(authHeader);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void testRegisterValidUser() {

    AuthResponse response = authService.register(registerRequest);

    assertNotNull(response);
    assertEquals(token, response.getToken());
    assertEquals("user", response.getUsername());
    assertEquals("user@example.com", response.getEmail());
    assertEquals("USER", response.getRole());

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterNullUsername() {

    registerRequest.setUsername(null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      authService.register(registerRequest);
    });

    assertEquals("Username and password are required", exception.getMessage());
  }

  @Test
  void testRegisterNullPassword() {

    registerRequest.setPassword(null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      authService.register(registerRequest);
    });

    assertEquals("Username and password are required", exception.getMessage());
  }

  @Test
  void testRegisterUsernameAlreadyExists() {

    when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

    UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
      authService.register(registerRequest);
    });

    assertEquals("Username already exists", exception.getMessage());
  }

  @Test
  void testRegisterEmailAlreadyExists() {

    when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

    UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
      authService.register(registerRequest);
    });

    assertEquals("User with this email already exists", exception.getMessage());
  }

}

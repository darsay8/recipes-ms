package dev.rm.recipes.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import dev.rm.recipes.model.AuthResponse;
import dev.rm.recipes.model.LoginRequest;
import dev.rm.recipes.model.RegisterRequest;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.TokenValidationResponse;
import dev.rm.recipes.model.User;
import dev.rm.recipes.repository.UserRepository;
import dev.rm.recipes.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthResponse login(LoginRequest loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsername(),
              loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      String token = tokenProvider.generateToken(authentication);

      return AuthResponse.builder()
          .token(token)
          .username(authentication.getName())
          .roles(tokenProvider.getRolesFromToken(token))
          .build();

    } catch (BadCredentialsException e) {
      throw new AuthenticationException("Invalid username or password");
    }
  }

  public void logout(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      SecurityContextHolder.clearContext();
    }
  }

  public AuthResponse register(RegisterRequest registerRequest) {
    if (registerRequest.getUsername() == null || registerRequest.getPassword() == null) {
      throw new IllegalArgumentException("Username and password are required");
    }

    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      throw new UserAlreadyExistsException("Username already exists");
    }

    User newUser = User.builder()
        .username(registerRequest.getUsername())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .email(registerRequest.getEmail())
        .role(Role.USER)
        .build();

    userRepository.save(newUser);

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            newUser.getUsername(),
            registerRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = tokenProvider.generateToken(authentication);

    return AuthResponse.builder()
        .token(token)
        .username(newUser.getUsername())
        .roles(newUser.getRole().name())
        .message("User registered and logged in successfully")
        .build();
  }

  public TokenValidationResponse validateToken(String token) {
    try {
      if (tokenProvider.validateToken(token)) {
        String username = tokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return TokenValidationResponse.builder()
            .valid(true)
            .username(username)
            .roles(tokenProvider.getRolesFromToken(token))
            .build();
      }
    } catch (Exception e) {
      // Token validation failed
    }

    return TokenValidationResponse.builder()
        .valid(false)
        .build();
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
      super(message);
    }
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
      super(message);
    }
  }
}
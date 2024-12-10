package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collection;
import java.util.Collections;

import dev.rm.recipes.config.SecurityConfig;
import dev.rm.recipes.model.AuthResponse;
import dev.rm.recipes.model.LoginRequest;
import dev.rm.recipes.model.RegisterRequest;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.TokenValidationResponse;
import dev.rm.recipes.model.User;
import dev.rm.recipes.security.CustomUserDetails;
import dev.rm.recipes.security.CustomUserDetailsService;
import dev.rm.recipes.security.JwtTokenProvider;
import dev.rm.recipes.service.AuthService;
import dev.rm.recipes.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuthService authService;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  CustomUserDetailsService customUserDetailsService;

  @MockBean
  private AuthenticationManager authenticationManager;

  private LoginRequest loginRequest;
  private RegisterRequest registerRequest;

  private TokenValidationResponse tokenValidationResponse;
  private String token;
  private User user;

  @BeforeEach
  void setUp() {

    user = User.builder()
        .userId(1L)
        .username("user")
        .email("user@example.com")
        .password("password")
        .role(Role.USER)
        .build();

    loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
    registerRequest = new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail());

    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

    CustomUserDetails userDetails = new CustomUserDetails(
        user.getEmail(),
        user.getUsername(),
        user.getPassword(),
        authorities);

    when(customUserDetailsService.loadUserByUsername(eq(user.getEmail()))).thenReturn(userDetails);

    token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzU3OTUyNywiZXhwIjoxNzMzNjY1OTI3fQ.g_IcKA8hcX6KZmCkzy7cwkjDw440dhob-cwTW0q3VMI";
    when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(user.getEmail());

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(user);
    when(jwtTokenProvider.generateToken(authentication)).thenReturn(token);
  }

  @Test
  void testLogin() throws Exception {

    AuthResponse authResponse = new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    when(authService.login(loginRequest)).thenReturn(authResponse);

    String loginRequestJson = "{ \"email\": \"user@example.com\", \"password\": \"password\" }";

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginRequestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(token))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andExpect(jsonPath("$.role").value(user.getRole().name()));

    verify(authService, times(1)).login(any(LoginRequest.class));
  }

  @Test
  void testRegister() throws Exception {

    AuthResponse authResponse = new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    when(authService.register(registerRequest)).thenReturn(authResponse);

    String registerRequestJson = "{ \"username\": \"user\", \"password\": \"password\", \"email\": \"user@example.com\" }";

    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registerRequestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(token))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andExpect(jsonPath("$.role").value(user.getRole().name()));

    verify(authService, times(1)).register(any(RegisterRequest.class));
  }

  @Test
  void testLogout() throws Exception {
    // Simulate a valid token in the Authorization header
    String authHeader = "Bearer " + token;

    // Perform the logout request
    mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
        .header("Authorization", authHeader))
        .andExpect(status().isOk());

    verify(authService, times(1)).logout(authHeader);
  }

  @Test
  void testValidateTokenWithoutAuthorization() throws Exception {

    when(authService.validateToken(anyString())).thenReturn(tokenValidationResponse);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/validate"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valid").value(false));

    verify(authService, never()).validateToken(anyString());
  }

}

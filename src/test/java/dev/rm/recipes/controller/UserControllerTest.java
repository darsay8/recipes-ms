package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import dev.rm.recipes.config.SecurityConfig;
import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import dev.rm.recipes.security.CustomUserDetails;
import dev.rm.recipes.security.CustomUserDetailsService;

import dev.rm.recipes.security.JwtTokenProvider;
import dev.rm.recipes.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  CustomUserDetailsService customUserDetailsService;

  @MockBean
  private AuthenticationManager authenticationManager;

  private User user;
  private String token;

  @BeforeEach
  void setUp() {

    user = User.builder()
        .userId(1L)
        .username("user")
        .email("user@example.com")
        .password("password")
        .role(Role.USER)
        .build();

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
  void testGetAllUsers_Authenticated() throws Exception {

    List<User> users = Arrays.asList(user);
    when(userService.getAllUsers()).thenReturn(users);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(users.size()));

    verify(userService, times(1)).getAllUsers();
  }

  @Test
  void testGetAllUsers_NoContent() throws Exception {

    when(userService.getAllUsers()).thenReturn(Collections.emptyList());

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    verify(userService, times(1)).getAllUsers();
  }

  @Test
  void testGetUserById() throws Exception {

    when(userService.getUserById(1L)).thenReturn(user);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1")
        .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user"));

    verify(userService, times(1)).getUserById(1L);
  }

  @Test
  void testGetUserById_NotFound() throws Exception {

    when(userService.getUserById(1L)).thenThrow(new RuntimeException("User notfound"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1")
        .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().string(""));

    verify(userService, times(1)).getUserById(1L);
  }

  @Test
  void testCreateUser() throws Exception {

    User createdUser = User.builder()
        .userId(2L)
        .username("newuser")
        .password("password")
        .email("newuser@example.com")
        .role(Role.USER)
        .build();

    when(userService.createUser(any(User.class))).thenReturn(createdUser);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{ \"username\": \"newuser\", \"email\": \"newuser@example.com\",\"password\": \"password\" }"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("newuser"));

    verify(userService, times(1)).createUser(any(User.class));
  }

  @Test
  void testDeleteUser() throws Exception {

    doNothing().when(userService).deleteUser(1L);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1")
        .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    verify(userService, times(1)).deleteUser(1L);
  }

  @Test
  void testDeleteUser_NotFound() throws Exception {

    doThrow(new RuntimeException("User notfound")).when(userService).deleteUser(1L);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1")
        .header("Authorization", "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.content().string(""));

    verify(userService, times(1)).deleteUser(1L);
  }

}

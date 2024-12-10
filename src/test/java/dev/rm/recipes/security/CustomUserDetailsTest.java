package dev.rm.recipes.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

  @Test
  void testGetters() {

    String email = "test@example.com";
    String username = "testuser";
    String password = "password";
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    CustomUserDetails userDetails = new CustomUserDetails(email, username, password,
        Collections.singletonList(authority));

    assertEquals(email, userDetails.getUsername());
    assertEquals(username, userDetails.getActualUsername());
    assertEquals(password, userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().contains(authority));
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());
  }
}

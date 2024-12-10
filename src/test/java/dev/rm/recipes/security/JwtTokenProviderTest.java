package dev.rm.recipes.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import dev.rm.recipes.model.Role;
import dev.rm.recipes.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
class JwtTokenProviderTest {
  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private CustomUserDetailsService customUserDetailsService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private Authentication authentication;

  private String token;
  private User user;
  private CustomUserDetails customUserDetails;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = User.builder()
        .userId(1L)
        .username("user")
        .email("user@example.com")
        .password("password")
        .role(Role.USER)
        .build();

    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

    customUserDetails = new CustomUserDetails(
        user.getEmail(),
        user.getUsername(),
        user.getPassword(),
        authorities);

    when(customUserDetailsService.loadUserByUsername(eq(user.getEmail()))).thenReturn(customUserDetails);
    when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(user.getEmail());
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    when(jwtTokenProvider.generateToken(authentication)).thenReturn(token);

    jwtTokenProvider = new JwtTokenProvider("FRs84cyivbEVgNnE3pg3gNUR5eOtK1XkropdlgS5bfg=", 3600000);

    token = jwtTokenProvider.generateToken(authentication);
  }

  @Test
  void testGetUsernameFromToken() {

    String username = jwtTokenProvider.getUsernameFromToken(token);

    assertEquals(user.getUsername(), username);
  }

  @Test
  void testGetEmailFromToken() {

    String email = jwtTokenProvider.getEmailFromToken(token);

    assertEquals(user.getEmail(), email);
  }

  @Test
  void testExtractClaim() {

    String emailClaim = jwtTokenProvider.extractAllClaims(token).get("email", String.class);

    assertTrue(emailClaim.contains(user.getEmail()));
  }

  @Test
  void testConstructorWithInvalidSecret() {

    assertThrows(IllegalArgumentException.class, () -> {
      new JwtTokenProvider("", 3600000); // Empty secret
    });

    assertThrows(IllegalArgumentException.class, () -> {
      new JwtTokenProvider(null, 3600000); // Null secret
    });
  }

  @Test
  void testConstructorWithInvalidExpirationTime() {

    assertThrows(IllegalArgumentException.class, () -> {
      new JwtTokenProvider("valid-secret", 0); // Expiration time 0
    });

    assertThrows(IllegalArgumentException.class, () -> {
      new JwtTokenProvider("valid-secret", -3600000); // Negative expiration time
    });
  }
}

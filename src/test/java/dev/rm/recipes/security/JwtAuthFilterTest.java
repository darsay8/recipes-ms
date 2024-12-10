package dev.rm.recipes.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private CustomUserDetailsService customUserDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private JwtAuthFilter jwtAuthFilter;

  @Test
  void testDoFilterInternal_ValidToken() throws Exception {

    String jwt = "valid-jwt-token";
    String email = "test@example.com";
    UserDetails userDetails = mock(UserDetails.class);
    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtTokenProvider.validateToken(jwt)).thenReturn(true);
    when(jwtTokenProvider.getEmailFromToken(jwt)).thenReturn(email);
    when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(jwtTokenProvider).validateToken(jwt);
    verify(customUserDetailsService).loadUserByUsername(email);
  }

  @Test
  void testDoFilterInternal_InvalidToken() throws Exception {

    String jwt = "invalid-jwt-token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtTokenProvider.validateToken(jwt)).thenReturn(false);

    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    verify(jwtTokenProvider).validateToken(jwt);
    verify(customUserDetailsService, never()).loadUserByUsername(anyString());
  }

}

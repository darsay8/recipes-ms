package dev.rm.recipes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

  private final SecretKey key;
  private final long jwtExpirationInMs;

  public JwtTokenProvider(
      @Value("${spring.security.jwt.secret}") String jwtSecret,
      @Value("${spring.security.jwt.expiration}") long jwtExpirationInMs) {
    if (jwtSecret == null || jwtSecret.isEmpty()) {
      throw new IllegalArgumentException("JWT secret is not configured");
    }
    if (jwtExpirationInMs <= 0) {
      throw new IllegalArgumentException("JWT expiration must be greater than zero");
    }

    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    this.jwtExpirationInMs = jwtExpirationInMs;

    log.debug("JWT Secret loaded: {}", jwtSecret);
  }

  public String generateToken(Authentication authentication) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

    String roles = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .claims()
        .subject(authentication.getName())
        .issuedAt(now)
        .expiration(expiryDate)
        .add("roles", roles)
        .and()
        .signWith(key)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public String getUsernameFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.getSubject();
  }

  public String getRolesFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("roles", String.class);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
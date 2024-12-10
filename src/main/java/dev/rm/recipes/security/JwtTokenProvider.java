package dev.rm.recipes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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

    // Ensure the key is at least 256 bits
    this.key = createSecureKey(jwtSecret);
    this.jwtExpirationInMs = jwtExpirationInMs;
  }

  private SecretKey createSecureKey(String secret) {
    try {
      // Hash the key to ensure it is 256 bits (32 bytes)
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashedKey = digest.digest(secret.getBytes(StandardCharsets.UTF_8));

      // Use the hashed key for HMAC
      return Keys.hmacShaKeyFor(hashedKey);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Unable to generate a secure key for JWT", e);
    }
  }

  public String generateToken(Authentication authentication) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

    String roles = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    String email = customUserDetails.getUsername();
    String username = customUserDetails.getActualUsername();

    return Jwts.builder()
        .claim("email", email)
        .claim("username", username)
        .claim("roles", roles)
        .subject(authentication.getName())
        .issuedAt(now)
        .expiration(expiryDate)
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
    return claims.get("username", String.class);
  }

  public String getEmailFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("email", String.class);
  }

  public String getRolesFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("roles", String.class);
  }

  public List<String> getRolesCollectionFromToken(String token) {
    Claims claims = extractAllClaims(token);

    return Arrays.asList(claims.get("roles", String[].class));
  }

  public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
    Claims claims = extractAllClaims(token);

    String[] roles = claims.get("roles", String[].class);

    return Arrays.stream(roles)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  public String extractClaim(String token, String claimKey) {
    Claims claims = extractAllClaims(token);
    return claims.get(claimKey, String.class);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
package dev.rm.recipes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.rm.recipes.model.AuthResponse;
import dev.rm.recipes.model.LoginRequest;
import dev.rm.recipes.model.RegisterRequest;
import dev.rm.recipes.model.TokenValidationResponse;
import dev.rm.recipes.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
    AuthResponse response = authService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
    AuthResponse response = authService.register(registerRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @RequestHeader(value = "Authorization", required = false) String authHeader) {
    authService.logout(authHeader);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/validate")
  public ResponseEntity<TokenValidationResponse> validateToken(
      @RequestHeader(value = "Authorization", required = false) String authHeader) {

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      TokenValidationResponse response = authService.validateToken(token);
      return ResponseEntity.ok(response);
    }

    return ResponseEntity.ok(TokenValidationResponse.builder()
        .valid(false)
        .build());
  }
}
package com.example.jsonplaceholderclone.controller;

import com.example.jsonplaceholderclone.model.User;
import com.example.jsonplaceholderclone.security.JwtTokenProvider;
import com.example.jsonplaceholderclone.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final UserService userService;

  public AuthController(
      final AuthenticationManager authenticationManager,
      final JwtTokenProvider tokenProvider,
      final UserService userService) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody final LoginRequest loginRequest) {
    final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    final String jwt = tokenProvider.generateToken(authentication);

    final Map<String, String> response = new HashMap<>();
    response.put("token", jwt);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody final User user) {
    if (userService.existsByUsername(user.getUsername())) {
      return ResponseEntity.badRequest().body("Username is already taken!");
    }

    if (userService.existsByEmail(user.getEmail())) {
      return ResponseEntity.badRequest().body("Email is already in use!");
    }

    final User createdUser = userService.createUser(user);
    return ResponseEntity.ok(createdUser);
  }
}

class LoginRequest {
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }
}

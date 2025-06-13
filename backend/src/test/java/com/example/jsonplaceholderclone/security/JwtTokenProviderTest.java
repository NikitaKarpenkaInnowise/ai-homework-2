package com.example.jsonplaceholderclone.security;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

  private JwtTokenProvider tokenProvider;
  private final String jwtSecret = "testSecretKey123456789012345678901234567890";
  private final int jwtExpirationInMs = 3600000; // 1 hour

  @BeforeEach
  void setUp() {
    tokenProvider = new JwtTokenProvider();
    org.springframework.test.util.ReflectionTestUtils.setField(
        tokenProvider, "jwtSecret", jwtSecret);
    org.springframework.test.util.ReflectionTestUtils.setField(
        tokenProvider, "jwtExpirationInMs", jwtExpirationInMs);
  }

  @Test
  void generateToken_ShouldCreateValidToken() {
    // Arrange
    final Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            "testuser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

    // Act
    final String token = tokenProvider.generateToken(authentication);

    // Assert
    assertNotNull(token);
    assertTrue(tokenProvider.validateToken(token));
  }

  @Test
  void getUsername_ShouldReturnUsernameFromToken() {
    // Arrange
    final String username = "testuser";
    final Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            username,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    final String token = tokenProvider.generateToken(authentication);

    // Act
    final String extractedUsername = tokenProvider.getUsername(token);

    // Assert
    assertEquals(username, extractedUsername);
  }

  @Test
  void validateToken_WithValidToken_ShouldReturnTrue() {
    // Arrange
    final Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            "testuser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    final String token = tokenProvider.generateToken(authentication);

    // Act
    final boolean isValid = tokenProvider.validateToken(token);

    // Assert
    assertTrue(isValid);
  }

  @Test
  void validateToken_WithInvalidToken_ShouldReturnFalse() {
    // Arrange
    final String invalidToken = "invalid.token.here";

    // Act
    final boolean isValid = tokenProvider.validateToken(invalidToken);

    // Assert
    assertFalse(isValid);
  }

  @Test
  void validateToken_WithExpiredToken_ShouldReturnFalse() {
    // Arrange
    final JwtTokenProvider expiredTokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(expiredTokenProvider, "jwtSecret", jwtSecret);
    ReflectionTestUtils.setField(
        expiredTokenProvider, "jwtExpirationInMs", 0L); // Expired immediately

    final Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            "testuser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    final String expiredToken = expiredTokenProvider.generateToken(authentication);

    // Act
    final boolean isValid = tokenProvider.validateToken(expiredToken);

    // Assert
    assertFalse(isValid);
  }

  @Test
  void getClaims_ShouldReturnValidClaims() {
    // Arrange
    final String username = "testuser";
    final Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            username,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    final String token = tokenProvider.generateToken(authentication);

    // Act
    final Claims claims = tokenProvider.getClaims(token);

    // Assert
    assertNotNull(claims);
    assertEquals(username, claims.getSubject());
  }
}

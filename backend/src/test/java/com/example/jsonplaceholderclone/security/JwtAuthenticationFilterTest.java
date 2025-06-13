package com.example.jsonplaceholderclone.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtTokenProvider tokenProvider;

  @Mock private UserDetailsService userDetailsService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private UserDetails userDetails;

  private JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_WithValidToken_ShouldSetAuthentication()
      throws ServletException, IOException {
    // Arrange
    final String token = "valid.jwt.token";
    final String username = "testuser";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.getUsername(token)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(tokenProvider, times(1)).validateToken(token);
    verify(tokenProvider, times(1)).getUsername(token);
    verify(userDetailsService, times(1)).loadUserByUsername(username);
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication()
      throws ServletException, IOException {
    // Arrange
    final String token = "invalid.jwt.token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.validateToken(token)).thenReturn(false);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(tokenProvider, times(1)).validateToken(token);
    verify(tokenProvider, never()).getUsername(any());
    verify(userDetailsService, never()).loadUserByUsername(any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternal_WithNoToken_ShouldNotSetAuthentication()
      throws ServletException, IOException {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn(null);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(tokenProvider, never()).validateToken(any());
    verify(tokenProvider, never()).getUsername(any());
    verify(userDetailsService, never()).loadUserByUsername(any());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  void doFilterInternal_WithInvalidBearerFormat_ShouldNotSetAuthentication()
      throws ServletException, IOException {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(tokenProvider, never()).validateToken(any());
    verify(tokenProvider, never()).getUsername(any());
    verify(userDetailsService, never()).loadUserByUsername(any());
    verify(filterChain, times(1)).doFilter(request, response);
  }
}

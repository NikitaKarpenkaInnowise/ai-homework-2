package com.example.jsonplaceholderclone.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.jsonplaceholderclone.config.TestSecurityConfig;
import com.example.jsonplaceholderclone.model.User;
import com.example.jsonplaceholderclone.security.JwtTokenProvider;
import com.example.jsonplaceholderclone.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthenticationManager authenticationManager;

  @MockBean private JwtTokenProvider tokenProvider;

  @MockBean private UserService userService;

  @Test
  void login_WithValidCredentials_ShouldReturnToken() throws Exception {
    // Arrange
    final LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("testuser");
    loginRequest.setPassword("password");

    final Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("test.jwt.token");

    // Act & Assert
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("test.jwt.token"));

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(tokenProvider, times(1)).generateToken(any(Authentication.class));
  }

  @Test
  void register_WithNewUser_ShouldReturnCreatedUser() throws Exception {
    // Arrange
    final User user = createSampleUser();
    when(userService.existsByUsername(anyString())).thenReturn(false);
    when(userService.existsByEmail(anyString())).thenReturn(false);
    when(userService.createUser(any(User.class))).thenReturn(user);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value(user.getUsername()));

    verify(userService, times(1)).existsByUsername(user.getUsername());
    verify(userService, times(1)).existsByEmail(user.getEmail());
    verify(userService, times(1)).createUser(any(User.class));
  }

  @Test
  void register_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
    // Arrange
    final User user = createSampleUser();
    when(userService.existsByUsername(anyString())).thenReturn(true);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username is already taken!"));

    verify(userService, times(1)).existsByUsername(user.getUsername());
    verify(userService, never()).createUser(any(User.class));
  }

  @Test
  void register_WithExistingEmail_ShouldReturnBadRequest() throws Exception {
    // Arrange
    final User user = createSampleUser();
    when(userService.existsByUsername(anyString())).thenReturn(false);
    when(userService.existsByEmail(anyString())).thenReturn(true);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Email is already in use!"));

    verify(userService, times(1)).existsByUsername(user.getUsername());
    verify(userService, times(1)).existsByEmail(user.getEmail());
    verify(userService, never()).createUser(any(User.class));
  }

  private User createSampleUser() {
    final User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setPassword("hashedPassword");
    return user;
  }
}

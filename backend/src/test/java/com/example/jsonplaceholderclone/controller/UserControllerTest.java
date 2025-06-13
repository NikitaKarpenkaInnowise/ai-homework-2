package com.example.jsonplaceholderclone.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.jsonplaceholderclone.config.TestSecurityConfig;
import com.example.jsonplaceholderclone.model.Address;
import com.example.jsonplaceholderclone.model.Company;
import com.example.jsonplaceholderclone.model.User;
import com.example.jsonplaceholderclone.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;

  @Test
  @WithMockUser
  void getAllUsers_ShouldReturnUsers() throws Exception {
    final List<User> users = Arrays.asList(createSampleUser(1L), createSampleUser(2L));
    when(userService.getAllUsers()).thenReturn(users);

    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2));

    verify(userService, times(1)).getAllUsers();
  }

  @Test
  @WithMockUser
  void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
    final User user = createSampleUser(1L);
    when(userService.getUserById(1L)).thenReturn(Optional.of(user));

    mockMvc
        .perform(get("/api/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value(user.getName()));

    verify(userService, times(1)).getUserById(1L);
  }

  @Test
  @WithMockUser
  void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
    when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/users/1")).andExpect(status().isNotFound());

    verify(userService, times(1)).getUserById(1L);
  }

  @Test
  @WithMockUser
  void createUser_ShouldReturnCreatedUser() throws Exception {
    final User user = createSampleUser(1L);
    when(userService.createUser(any(User.class))).thenReturn(user);

    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value(user.getName()));

    verify(userService, times(1)).createUser(any(User.class));
  }

  @Test
  @WithMockUser
  void updateUser_WhenUserExists_ShouldReturnUpdatedUser() throws Exception {
    final User user = createSampleUser(1L);
    when(userService.updateUser(anyLong(), any(User.class))).thenReturn(user);

    mockMvc
        .perform(
            put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value(user.getName()));

    verify(userService, times(1)).updateUser(eq(1L), any(User.class));
  }

  @Test
  @WithMockUser
  void updateUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
    final User user = createSampleUser(1L);
    when(userService.updateUser(anyLong(), any(User.class)))
        .thenThrow(new RuntimeException("User not found"));

    mockMvc
        .perform(
            put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).updateUser(eq(1L), any(User.class));
  }

  @Test
  @WithMockUser
  void deleteUser_ShouldReturnOk() throws Exception {
    doNothing().when(userService).deleteUser(anyLong());

    mockMvc.perform(delete("/api/users/1")).andExpect(status().isOk());

    verify(userService, times(1)).deleteUser(1L);
  }

  private User createSampleUser(final Long id) {
    final User user = new User();
    user.setId(id);
    user.setName("Test User");
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setPhone("1234567890");
    user.setWebsite("www.example.com");

    final Address address = new Address();
    address.setId(id);
    address.setStreet("Test Street");
    address.setSuite("Test Suite");
    address.setCity("Test City");
    address.setZipcode("12345");
    user.setAddress(address);

    final Company company = new Company();
    company.setId(id);
    company.setName("Test Company");
    company.setCatchPhrase("Test Catch Phrase");
    company.setBs("Test BS");
    user.setCompany(company);

    return user;
  }
}

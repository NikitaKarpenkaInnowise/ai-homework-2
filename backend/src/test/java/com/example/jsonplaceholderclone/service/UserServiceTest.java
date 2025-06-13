package com.example.jsonplaceholderclone.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.jsonplaceholderclone.model.Address;
import com.example.jsonplaceholderclone.model.Company;
import com.example.jsonplaceholderclone.model.User;
import com.example.jsonplaceholderclone.repository.UserRepository;
import com.example.jsonplaceholderclone.service.impl.UserServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserServiceImpl(userRepository, passwordEncoder);
  }

  @Test
  void getAllUsers_ShouldReturnAllUsers() {
    // Arrange
    final List<User> expectedUsers = Arrays.asList(createSampleUser(1L), createSampleUser(2L));
    when(userRepository.findAll()).thenReturn(expectedUsers);

    // Act
    final List<User> actualUsers = userService.getAllUsers();

    // Assert
    assertEquals(expectedUsers.size(), actualUsers.size());
    assertEquals(expectedUsers.get(0).getId(), actualUsers.get(0).getId());
    assertEquals(expectedUsers.get(1).getId(), actualUsers.get(1).getId());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void getUserById_WhenUserExists_ShouldReturnUser() {
    // Arrange
    final User expectedUser = createSampleUser(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

    // Act
    final Optional<User> actualUser = userService.getUserById(1L);

    // Assert
    assertTrue(actualUser.isPresent());
    assertEquals(expectedUser.getId(), actualUser.get().getId());
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Arrange
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act
    final Optional<User> actualUser = userService.getUserById(1L);

    // Assert
    assertTrue(actualUser.isEmpty());
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void createUser_ShouldHashPasswordAndSaveUser() {
    // Arrange
    final User user = createSampleUser(1L);
    user.setPassword("plainPassword");
    when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    // Act
    final User createdUser = userService.createUser(user);

    // Assert
    assertEquals("hashedPassword", createdUser.getPassword());
    verify(passwordEncoder, times(1)).encode("plainPassword");
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() {
    // Arrange
    final User existingUser = createSampleUser(1L);
    final User updatedUser = createSampleUser(1L);
    updatedUser.setName("Updated Name");
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // Act
    final User result = userService.updateUser(1L, updatedUser);

    // Assert
    assertEquals("Updated Name", result.getName());
    verify(userRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
    // Arrange
    final User user = createSampleUser(1L);
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> userService.updateUser(1L, user));
    verify(userRepository, times(1)).findById(1L);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void deleteUser_ShouldCallRepository() {
    // Act
    userService.deleteUser(1L);

    // Assert
    verify(userRepository, times(1)).deleteById(1L);
  }

  @Test
  void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
    // Arrange
    when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

    // Act
    final boolean exists = userService.existsByEmail("test@example.com");

    // Assert
    assertTrue(exists);
    verify(userRepository, times(1)).existsByEmail("test@example.com");
  }

  @Test
  void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
    // Arrange
    when(userRepository.existsByUsername("testuser")).thenReturn(true);

    // Act
    final boolean exists = userService.existsByUsername("testuser");

    // Assert
    assertTrue(exists);
    verify(userRepository, times(1)).existsByUsername("testuser");
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

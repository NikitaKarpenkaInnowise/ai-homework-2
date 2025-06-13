package com.example.jsonplaceholderclone.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.jsonplaceholderclone.model.Address;
import com.example.jsonplaceholderclone.model.Company;
import com.example.jsonplaceholderclone.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class UserRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private UserRepository userRepository;

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    // Arrange
    final User user = createSampleUser();
    entityManager.persist(user);
    entityManager.flush();

    // Act
    final Optional<User> found = userRepository.findByUsername(user.getUsername());

    // Assert
    assertTrue(found.isPresent());
    assertEquals(user.getUsername(), found.get().getUsername());
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    // Act
    final Optional<User> found = userRepository.findByUsername("nonexistent");

    // Assert
    assertTrue(found.isEmpty());
  }

  @Test
  void existsByUsername_WhenUserExists_ShouldReturnTrue() {
    // Arrange
    final User user = createSampleUser();
    entityManager.persist(user);
    entityManager.flush();

    // Act
    final boolean exists = userRepository.existsByUsername(user.getUsername());

    // Assert
    assertTrue(exists);
  }

  @Test
  void existsByUsername_WhenUserDoesNotExist_ShouldReturnFalse() {
    // Act
    final boolean exists = userRepository.existsByUsername("nonexistent");

    // Assert
    assertFalse(exists);
  }

  @Test
  void existsByEmail_WhenUserExists_ShouldReturnTrue() {
    // Arrange
    final User user = createSampleUser();
    entityManager.persist(user);
    entityManager.flush();

    // Act
    final boolean exists = userRepository.existsByEmail(user.getEmail());

    // Assert
    assertTrue(exists);
  }

  @Test
  void existsByEmail_WhenUserDoesNotExist_ShouldReturnFalse() {
    // Act
    final boolean exists = userRepository.existsByEmail("nonexistent@example.com");

    // Assert
    assertFalse(exists);
  }

  private User createSampleUser() {
    final User user = new User();
    user.setName("Test User");
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setPhone("1234567890");
    user.setWebsite("www.example.com");
    user.setPassword("hashedPassword");

    final Address address = new Address();
    address.setStreet("Test Street");
    address.setSuite("Test Suite");
    address.setCity("Test City");
    address.setZipcode("12345");
    user.setAddress(address);

    final Company company = new Company();
    company.setName("Test Company");
    company.setCatchPhrase("Test Catch Phrase");
    company.setBs("Test BS");
    user.setCompany(company);

    return user;
  }
}

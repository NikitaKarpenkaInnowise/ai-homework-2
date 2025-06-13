package com.example.jsonplaceholderclone.service.impl;

import com.example.jsonplaceholderclone.model.User;
import com.example.jsonplaceholderclone.repository.UserRepository;
import com.example.jsonplaceholderclone.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(
      final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> getUserById(final Long id) {
    return userRepository.findById(id);
  }

  @Override
  public Optional<User> getUserByEmail(final String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<User> getUserByUsername(final String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public User createUser(final User user) {
    if (user.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(user);
  }

  @Override
  public User updateUser(final Long id, final User user) {
    return userRepository
        .findById(id)
        .map(
            existingUser -> {
              existingUser.setName(user.getName());
              existingUser.setUsername(user.getUsername());
              existingUser.setEmail(user.getEmail());
              existingUser.setAddress(user.getAddress());
              existingUser.setPhone(user.getPhone());
              existingUser.setWebsite(user.getWebsite());
              existingUser.setCompany(user.getCompany());
              if (user.getPassword() != null) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
              }
              return userRepository.save(existingUser);
            })
        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  @Override
  public void deleteUser(final Long id) {
    userRepository.deleteById(id);
  }

  @Override
  public boolean existsByEmail(final String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public boolean existsByUsername(final String username) {
    return userRepository.existsByUsername(username);
  }
}

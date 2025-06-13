package com.example.jsonplaceholderclone.service;

import com.example.jsonplaceholderclone.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

  List<User> getAllUsers();

  Optional<User> getUserById(final Long id);

  Optional<User> getUserByEmail(final String email);

  Optional<User> getUserByUsername(final String username);

  User createUser(final User user);

  User updateUser(final Long id, final User user);

  void deleteUser(final Long id);

  boolean existsByEmail(final String email);

  boolean existsByUsername(final String username);
}

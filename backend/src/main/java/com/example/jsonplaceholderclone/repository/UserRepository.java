package com.example.jsonplaceholderclone.repository;

import com.example.jsonplaceholderclone.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(final String email);

  Optional<User> findByUsername(final String username);

  boolean existsByEmail(final String email);

  boolean existsByUsername(final String username);
}

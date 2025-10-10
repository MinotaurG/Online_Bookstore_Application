package com.bookstore;

import java.util.List;
import java.util.Optional;

/** Repository interface for persisting and retrieving User entities. */
public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findAll();
}
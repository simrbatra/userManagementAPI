package com.example.usermanagementapi.repository;

import com.example.usermanagementapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Optional: Uncomment if you want to search by name (case-insensitive)
    // List<User> findByNameContainingIgnoreCase(String name);
}

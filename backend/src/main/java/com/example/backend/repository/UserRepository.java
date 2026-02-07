package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Find user by username OR email (for login)
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // Find user by email
    Optional<User> findByEmail(String email);

    // Save user (already provided by JpaRepository)
    // User save(User user);
}

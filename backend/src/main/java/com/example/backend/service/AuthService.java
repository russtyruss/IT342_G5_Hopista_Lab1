package com.example.backend.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.entity.User;
import com.example.backend.repository.TokenRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.TokenProvider;

@Service
public class AuthService {

    public final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       TokenProvider tokenProvider,
                       TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // BCrypt
    }

    // ------------------------
    // Register a new user
    // ------------------------
    public User registerUser(User user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user in DB
        return userRepository.save(user);
    }

    // ------------------------
    // Authenticate user
    // ------------------------
    public String authenticate(String usernameOrEmail, String password) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid username/email or password");
        }

        User user = optionalUser.get();

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username/email or password");
        }

        // Generate access token
        String accessToken = tokenProvider.generateAccessToken(user);

        // Generate refresh token
        String refreshToken = tokenProvider.generateRefreshToken(user);

        return accessToken; // you can return both tokens in a DTO if you like
    }

    // ------------------------
    // Authenticate and issue both tokens
    // ------------------------
    public AuthResponse login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid username/email or password");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username/email or password");
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    // ------------------------
    // Refresh access token
    // ------------------------
    public String refreshToken(String tokenValue) {
        if (!tokenProvider.validateRefreshToken(tokenValue)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Find user from token
        User user = tokenProvider.getUserFromRefreshToken(tokenValue);

        // Generate new access token
        return tokenProvider.generateAccessToken(user);
    }
}

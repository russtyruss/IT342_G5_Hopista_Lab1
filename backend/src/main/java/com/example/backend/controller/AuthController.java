package main.java.com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.User;
import com.example.backend.service.AuthService;
import com.example.backend.security.TokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenProvider tokenProvider;

    public AuthController(AuthService authService, TokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    // ------------------------
    // Register endpoint
    // ------------------------
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = authService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // ------------------------
    // Login endpoint
    // ------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String accessToken = authService.authenticate(request.getUsernameOrEmail(), request.getPassword());
        String refreshToken = tokenProvider.generateRefreshToken(authService.userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), request.getUsernameOrEmail()).get()
        );

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    // ------------------------
    // Refresh endpoint
    // ------------------------
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String newAccessToken = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new AuthResponse(newAccessToken, request.getRefreshToken()));
    }

    // ------------------------
    // Logout endpoint
    // ------------------------
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        tokenProvider.invalidate(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}

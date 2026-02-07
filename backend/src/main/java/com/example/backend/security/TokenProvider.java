package com.example.backend.security;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.backend.entity.Token;
import com.example.backend.entity.User;
import com.example.backend.repository.TokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider {

    // Minimum 64 bytes (>= 512 bits) for HS512
    private final String jwtSecret = "this-is-a-very-long-secret-for-hs512-keep-it-safe-0123456789-ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // TODO: move to application.properties
    private final TokenRepository tokenRepository;

    public TokenProvider(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Generate JWT access token
    public String generateAccessToken(User user) {
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 min
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Generate refresh token
    public String generateRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();

        Token token = new Token();
        token.setTokenValue(tokenValue);
        token.setUser(user);
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setActive(true);

        tokenRepository.save(token);

        return tokenValue;
    }

    // Validate refresh token
    public boolean validateRefreshToken(String tokenValue) {
        return tokenRepository.findByTokenValue(tokenValue)
                .filter(Token::isActive)
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    // Invalidate refresh token
    public void invalidate(String tokenValue) {
        tokenRepository.findByTokenValue(tokenValue).ifPresent(t -> {
            t.setActive(false);
            tokenRepository.save(t);
        });
    }

    // Get user from refresh token
    public User getUserFromRefreshToken(String tokenValue) {
        return tokenRepository.findByTokenValue(tokenValue)
                .map(Token::getUser)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }
}

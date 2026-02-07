package com.example.backend.repository;

import com.example.backend.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    // Delete all tokens by user ID
    void deleteByUserId(Integer userId);

    // Find token by tokenValue
    Optional<Token> findByTokenValue(String tokenValue);

    // Save token (already provided by JpaRepository)
    // Token save(Token token);
}

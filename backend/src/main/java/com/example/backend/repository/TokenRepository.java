package main.java.com.example.backend.repository;

import com.example.backend.entity.Token;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    // Delete all tokens by user ID
    void deleteByUserId(Integer userId);

    // Find token by tokenValue
    Optional<Token> findByTokenValue(String tokenValue);

    // Save token (already provided by JpaRepository)
    // Token save(Token token);
}

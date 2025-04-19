package com.easygame.api.security;

import com.easygame.api.configuration.HmacProperties;
import com.easygame.api.exception.InvalidTokenException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class GameScoreTokenProviderTest {

    private GameScoreTokenProvider provider;

    @BeforeEach
    void setUp() {
        // Hardcoded secret key for testing purposes (must be secured in production)
        HmacProperties testProps = new HmacProperties();
        testProps.setSecretKey("test-secret-key");

        provider = new GameScoreTokenProvider(testProps);
    }

    @DisplayName("Success: Generate and validate a signed HMAC token")
    @Test
    void generateAndValidateToken_shouldSucceed() throws Exception {
        String gameType = "RABBIT";
        String nickname = "test1";
        int score = 1500;
        String jti = "test1-jti";

        String signedToken = provider.generate(gameType,nickname, score, jti);

        assertDoesNotThrow(() -> provider.isValidSignedToken(gameType, nickname, score, jti, signedToken));
    }

    @DisplayName("Failure: Validation fails with a tampered signedToken")
    @Test
    void tamperedToken_shouldThrowException() throws Exception {
        String gameType = "RABBIT";
        String nickname = "test1";
        int score = 1500;
        String jti = "test1-jti";

        String tamperedToken = "invalid-token";

        InvalidTokenException ex = assertThrows(InvalidTokenException.class, () -> {
            provider.isValidSignedToken(gameType, nickname, score, jti, tamperedToken);
        });
    }

    @DisplayName("Failure: Validation fails when score is modified")
    @Test
    void tamperedScore_shouldThrowException() throws Exception {
        String gameType = "RABBIT";
        String nickname = "test1";
        int score = 1500;
        String jti = "test1-jti";
        int tamperedScore = 2000;

        String signedToken = provider.generate(gameType, nickname, score, jti);

        assertThrows(InvalidTokenException.class, () -> {
            provider.isValidSignedToken(gameType, nickname, tamperedScore, jti, signedToken);
        });
    }

    @DisplayName("Failure: Validation fails when jti is modified")
    @Test
    void tamperedJti_shouldThrowException() throws Exception {
        String gameType = "RABBIT";
        String nickname = "test1";
        int score = 1500;
        String jti = "test1-jti";

        String signedToken = provider.generate(gameType, nickname, score, jti);

        assertThrows(InvalidTokenException.class, () -> {
            provider.isValidSignedToken(gameType, nickname, score, "fake-jti", signedToken);
        });
    }
}

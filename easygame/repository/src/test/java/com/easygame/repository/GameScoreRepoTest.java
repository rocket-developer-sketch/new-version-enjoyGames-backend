package com.easygame.repository;

import com.easygame.repository.type.GameType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@BaseTestConfig
public class GameScoreRepoTest {
    @Autowired
    private GameScoreRepository repository;

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Throws exception when nickName is null during build")
    void build_withNullNickname_shouldThrowException() {
        assertThatThrownBy(() -> GameScore.builder()
                .nickName(null)
                .jti("abc-jti")
                .score(100)
                .gameType(GameType.RABBIT)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nickName must not be null or empty");
    }

    @Test
    @DisplayName("Throws exception when jti is empty during build")
    void build_withEmptyJti_shouldThrowException() {
        assertThatThrownBy(() -> GameScore.builder()
                .nickName("player")
                .jti("")
                .score(100)
                .gameType(GameType.RABBIT)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jti must not be null or empty");
    }

    @Test
    @DisplayName("Throws exception when gameType is null during build")
    void build_withNullGameType_shouldThrowException() {
        assertThatThrownBy(() -> GameScore.builder()
                .nickName("player")
                .jti("jti-123")
                .score(100)
                .gameType(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("gameType must not be null");
    }
}

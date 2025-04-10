package com.easygame.repository;

import com.easygame.repository.type.GameType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@BaseTestConfig
public class GameScoreRepoTest {
    @Autowired
    private GameScoreRepository repository;

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Throws exception when saving without user")
    void saveGameScoreWithoutUser_shouldFail() {
        GameScore score = GameScore.builder()
                .userId(null)
                .score(1000)
                .gameType(GameType.RABBIT)
                .build();

        assertThatThrownBy(() -> repository.save(score))
                .isInstanceOf(Exception.class); // or PersistenceException
    }

    @Test
    @DisplayName("Throws exception when saving user id is 0")
    void saveGameScoreWithUserIdZero_shouldFail() {
        GameScore score = GameScore.builder()
                .userId(0L)
                .score(1000)
                .gameType(GameType.RABBIT)
                .build();

        assertThatThrownBy(() -> repository.save(score))
                .isInstanceOf(Exception.class); // or PersistenceException
    }
}

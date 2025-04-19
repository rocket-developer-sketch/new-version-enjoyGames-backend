package com.easygame.service;


import com.easygame.repository.GameScore;
import com.easygame.repository.GameScoreRepository;
import com.easygame.repository.type.GameType;
import com.easygame.service.dto.GameScoreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GameScoreServiceTest {

    @Mock
    private GameScoreRepository gameScoreRepository;

    @InjectMocks
    private GameScoreService gameScoreService;

    @Test
    @DisplayName("Should save score successfully when given a valid score")
    void saveScore_shouldSaveValidScore() throws Exception {
        // given
        String nickName = "player1";
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .nickName(nickName)
                .jti("test-jti")
                .score(100)
                .gameTypeStr("RABBIT")
                .build();

        gameScoreService.saveScore(gameScoreDto);

        // then
        verify(gameScoreRepository, times(1)).save(any(GameScore.class));
    }

    @Test
    @DisplayName("Should throw exception when score is negative")
    void saveScore_shouldThrowException_whenScoreIsNegative() throws Exception {
        // given
        String nickName = "testUser";
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .nickName(nickName)
                .jti("test-jti")
                .score(-100) // invalid score
                .gameTypeStr("RABBIT")
                .build();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            gameScoreService.saveScore(gameScoreDto);
        });

        // confirms it was never called
        verify(gameScoreRepository, never()).save(any());
    }

    @Test
    @DisplayName("Returns top N scores with dense ranking by game type")
    void getTopNScoresByGameType_shouldReturnDenseRankedScores() {
        // given
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .gameTypeStr("RABBIT")
                .top(5)
                .build();

        String user1 = "player1";
        String user2 = "player2";
        String user3 = "player3";
        String user4 = "player4";
        String user5 = "player5";

        GameScore gameScore1 = GameScore.builder().nickName(user1).jti(user1+"-jti").score(100).gameType(GameType.RABBIT).build();
        GameScore gameScore2 = GameScore.builder().nickName(user2).jti(user2+"-jti").score(80).gameType(GameType.RABBIT).build();
        GameScore gameScore3 = GameScore.builder().nickName(user3).jti(user3+"-jti").score(80).gameType(GameType.RABBIT).build();
        GameScore gameScore4 = GameScore.builder().nickName(user4).jti(user4+"-jti").score(70).gameType(GameType.RABBIT).build();
        GameScore gameScore5 = GameScore.builder().nickName(user5).jti(user5+"-jti").score(50).gameType(GameType.RABBIT).build();

        List<GameScore> mockScores = List.of(gameScore1, gameScore2, gameScore3, gameScore4, gameScore5);

        when(gameScoreRepository.findByGameType(eq(GameType.RABBIT), any(Pageable.class)))
                .thenReturn(mockScores);

        // when
        List<GameScoreDto> result = gameScoreService.getTop10ByGameType(gameScoreDto);

        // then
        assertEquals(5, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(2, result.get(1).getRank()); // Duplicate scores are considered
        assertEquals(2, result.get(2).getRank());
        assertEquals(3, result.get(3).getRank());
        assertEquals(4, result.get(4).getRank());
    }
    @Test
    @DisplayName("Should allow score of 0")
    void saveScore_shouldAllowZeroScore() throws Exception {
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .nickName("zeroUser")
                .jti("zero-jti")
                .score(0)
                .gameTypeStr("RABBIT")
                .build();

        gameScoreService.saveScore(gameScoreDto);

        verify(gameScoreRepository).save(any(GameScore.class));
    }

    @Test
    @DisplayName("Should throw exception when top value is zero")
    void getTop10ByGameType_shouldThrowException_whenTopIsZero() {
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .gameTypeStr("RABBIT")
                .top(0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> gameScoreService.getTop10ByGameType(gameScoreDto));
    }

    @Test
    @DisplayName("Should throw exception when top value is too large")
    void getTop10ByGameType_shouldThrowException_whenTopIsTooLarge() {
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .gameTypeStr("RABBIT")
                .top(1000)
                .build();

        assertThrows(IllegalArgumentException.class, () -> gameScoreService.getTop10ByGameType(gameScoreDto));
    }

    @Test
    @DisplayName("Should return empty list when no scores exist for game type")
    void getTop10ByGameType_shouldReturnEmptyList_whenNoScoresExist() {
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .gameTypeStr("RABBIT")
                .top(5)
                .build();

        when(gameScoreRepository.findByGameType(eq(GameType.RABBIT), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<GameScoreDto> result = gameScoreService.getTop10ByGameType(gameScoreDto);
        assertTrue(result.isEmpty());
    }

}

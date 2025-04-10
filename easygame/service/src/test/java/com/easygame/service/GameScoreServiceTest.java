package com.easygame.service;


import com.easygame.repository.GameScoreRepository;
import com.easygame.repository.GameScore;
import com.easygame.repository.type.GameType;
import com.easygame.service.dto.GameScoreDto;
import com.easygame.service.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GameScoreServiceTest {

    @Mock
    private GameScoreRepository gameScoreRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GameScoreService gameScoreService;

    @Test
    @DisplayName("Should save score successfully when given a valid score")
    void saveScore_shouldSaveValidScore() throws Exception {
        // given
        String nickName = "testUser";
        GameScoreDto gameScoreDto = GameScoreDto.builder()
                .nickName(nickName)
                .score(100)
                .gameTypeStr("RABBIT")
                .build();

        UserDto userDto = UserDto.builder()
                .userId(1L)
                .nickName(nickName)
                .build();

        // when
        when(userService.getOrThrow(nickName)).thenReturn(userDto);

        gameScoreService.saveScore(nickName, gameScoreDto);

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
                .score(-100) // invalid score
                .gameTypeStr("RABBIT")
                .build();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            gameScoreService.saveScore(nickName, gameScoreDto);
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

        long userId1 = 1;
        long userId2 = 2;
        long userId3 = 3;
        long userId4 = 4;
        long userId5 = 5;

        GameScore gameScore1 = GameScore.builder().userId(userId1).score(100).gameType(GameType.RABBIT).build();
        GameScore gameScore2 = GameScore.builder().userId(userId2).score(80).gameType(GameType.RABBIT).build();
        GameScore gameScore3 = GameScore.builder().userId(userId3).score(80).gameType(GameType.RABBIT).build();
        GameScore gameScore4 = GameScore.builder().userId(userId4).score(70).gameType(GameType.RABBIT).build();
        GameScore gameScore5 = GameScore.builder().userId(userId5).score(50).gameType(GameType.RABBIT).build();

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
}

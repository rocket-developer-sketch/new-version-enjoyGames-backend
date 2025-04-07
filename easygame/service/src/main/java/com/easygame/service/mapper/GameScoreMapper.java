package com.easygame.service.mapper;

import com.easygame.repository.GameScore;
import com.easygame.service.dto.GameScoreDto;
import org.springframework.stereotype.Component;


@Component
public class GameScoreMapper implements BaseMapper<GameScoreDto, GameScore> {
    @Override
    public GameScoreDto toDto(GameScore gameScore) {
        return GameScoreDto.builder()
                .nickName(gameScore.getNickName())
                .score(gameScore.getScore())
                .gameTypeStr(gameScore.getGameType().name())
                .build();
    }

    @Override
    public GameScore toEntity(GameScoreDto gameScoreDto) {
        return GameScore.builder()
                .nickName(gameScoreDto.getNickName())
                .score(gameScoreDto.getScore())
                .gameType(gameScoreDto.getGameType())
                .build();
    }
}

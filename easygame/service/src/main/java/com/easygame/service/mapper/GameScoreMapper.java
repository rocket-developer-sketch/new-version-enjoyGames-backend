package com.easygame.service.mapper;

import com.easygame.repository.GameScore;
import com.easygame.service.dto.GameScoreDto;
import org.springframework.stereotype.Component;


@Component
//public class GameScoreMapper implements BaseMapper<GameScoreDto, GameScoreOld> {
public class GameScoreMapper implements BaseMapper<GameScoreDto, GameScore> {
    @Override
    //public GameScoreDto toDto(GameScoreOld gameScore) {
    public GameScoreDto toDto(GameScore gameScore) {
        return GameScoreDto.builder()
                //.nickName(gameScore.getUser().getNickName())
                .nickName(gameScore.getNickName())
                .score(gameScore.getScore())
                .gameTypeStr(gameScore.getGameType().name())
                .build();
    }

    @Override
    //public GameScoreOld toEntity(GameScoreDto gameScoreDto) {
    public GameScore toEntity(GameScoreDto gameScoreDto) {
        return GameScore.builder()
                //.userId(gameScoreDto.getUserId())
                .nickName(gameScoreDto.getNickName())
                .score(gameScoreDto.getScore())
                .gameType(gameScoreDto.getGameType())
                .build();
    }
}

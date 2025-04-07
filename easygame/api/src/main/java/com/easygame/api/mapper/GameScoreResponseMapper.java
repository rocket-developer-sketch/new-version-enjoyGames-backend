package com.easygame.api.mapper;

import com.easygame.api.response.GameScoreTopResponse;
import com.easygame.service.dto.GameScoreDto;
import org.springframework.stereotype.Component;

@Component
public class GameScoreResponseMapper implements BaseMapper<GameScoreDto, GameScoreTopResponse> {
    @Override
    public GameScoreTopResponse toDto(GameScoreDto dto) {
        return GameScoreTopResponse.builder()
                .nickName(dto.getNickName())
                .score(dto.getScore())
                .gameType(dto.getGameTypeStr())
                .build();
    }
}

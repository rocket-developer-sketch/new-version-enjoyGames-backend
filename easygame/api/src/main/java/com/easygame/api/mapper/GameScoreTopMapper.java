package com.easygame.api.mapper;

import com.easygame.api.request.GameScoreTopRequest;
import com.easygame.service.dto.GameScoreDto;
import org.springframework.stereotype.Component;

@Component
public class GameScoreTopMapper {
    public GameScoreDto toDto(GameScoreTopRequest request) {
        return GameScoreDto.builder()
                .gameTypeStr(request.getGameType())
                .top(request.getTop())
                .build();
    }

}

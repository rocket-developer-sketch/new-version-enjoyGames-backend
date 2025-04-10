package com.easygame.api.mapper;

import com.easygame.api.request.GameScoreSaveRequest;
import com.easygame.service.dto.GameScoreDto;
import org.springframework.stereotype.Component;

@Component
public class GameScoreSaveMapper {
    public GameScoreDto toDto(GameScoreSaveRequest request) {
        return GameScoreDto.builder()
                .nickName(request.getNickName())
                .score(request.getScore())
                .gameTypeStr(request.getGameType())
                .build();
    }
}

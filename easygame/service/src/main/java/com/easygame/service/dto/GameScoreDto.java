package com.easygame.service.dto;

import com.easygame.repository.type.GameType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreDto {
    private Long scoreId;
    private Long userId;
    private String nickName;
    private int score;
    private String gameTypeStr;
    private int top;
    private int rank;

    @Builder
    public GameScoreDto(Long scoreId, Long userId, String nickName, int score, String gameTypeStr, int top, int rank) {
        this.scoreId = scoreId;
        this.userId = userId;
        this.nickName = nickName;
        this.score = score;
        this.gameTypeStr = gameTypeStr;
        this.top = top;
        this.rank = rank;
    }

    public GameType getGameType() {
        return GameType.from(gameTypeStr);
    }
}

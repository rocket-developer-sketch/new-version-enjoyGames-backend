package com.easygame.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreSaveRequest {
    private Long scoreId;
    private String nickName;
    private int score;
    private String gameType;

    @Builder
    public GameScoreSaveRequest(Long scoreId, String nickName, int score, String gameType) {
        this.scoreId = scoreId;
        this.nickName = nickName;
        this.score = score;
        this.gameType = gameType;
    }
}

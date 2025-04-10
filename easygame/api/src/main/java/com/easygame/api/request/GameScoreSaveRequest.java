package com.easygame.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreSaveRequest {
    private String nickName;
    private int score;
    private String gameType;

    @Builder
    public GameScoreSaveRequest(String nickName, int score, String gameType) {
        this.nickName = nickName;
        this.score = score;
        this.gameType = gameType;
    }
}

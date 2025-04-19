package com.easygame.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreSaveTokenRequest {
    private String gameType;
    private String jti;
    private int score;

    @Builder
    public GameScoreSaveTokenRequest(String gameType, String jti, int score) {
        this.gameType = gameType;
        this.jti = jti;
        this.score = score;
    }
}

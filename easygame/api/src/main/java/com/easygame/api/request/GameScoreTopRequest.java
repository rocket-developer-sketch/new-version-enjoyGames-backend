package com.easygame.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreTopRequest {
    private String gameType;
    private int top;

    @Builder
    public GameScoreTopRequest(String gameType, int top) {
        this.gameType = gameType;
        this.top = top;
    }
}

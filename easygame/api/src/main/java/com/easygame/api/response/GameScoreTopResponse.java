package com.easygame.api.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreTopResponse {
    private String nickName;
    private int score;
    private String gameType;
    private int rank;

    @Builder
    public GameScoreTopResponse(String nickName, int score, String gameType, int rank) {
        this.nickName = nickName;
        this.score = score;
        this.gameType = gameType;
        this.rank = rank;
    }
}

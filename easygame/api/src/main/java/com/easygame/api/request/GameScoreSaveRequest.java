package com.easygame.api.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreSaveRequest {
    private String gameType;
    private String nickName;
    private int score;
    private String jti;
    private String signedToken;

    @Builder
    public GameScoreSaveRequest(String gameType, String nickName, int score, String jti, String signedToken) {
        this.gameType = gameType;
        this.nickName = nickName;
        this.score = score;
        this.jti = jti;
        this.signedToken = signedToken;
    }

    @Override
    public String toString() {
        return "GameScoreSaveRequest{" +
                "gameType='" + gameType + '\'' +
                ", nickName='" + nickName + '\'' +
                ", score=" + score +
                ", jti='" + jti + '\'' +
                ", signedToken='" + signedToken + '\'' +
                '}';
    }
}

package com.easygame.api.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScoreTokenResponse {
    private String signedToken;
    private int score;
    private String jti;

    @Builder
    public GameScoreTokenResponse(String signedToken, int score, String jti) {
        this.signedToken = signedToken;
        this.score = score;
        this.jti = jti;
    }
}

package com.easygame.api.security;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtToken {
    private String token;
    private String gameType;
    private String jti;
    private String nickName;

    @Builder
    public JwtToken(String token, String gameType, String jti, String nickName) {
        this.token = token;
        this.gameType = gameType;
        this.jti = jti;
        this.nickName = nickName;

    }

    @Override
    public String toString() {
        return "JwtToken{" +
                "token='" + token + '\'' +
                ", gameType='" + gameType + '\'' +
                ", jti='" + jti + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}

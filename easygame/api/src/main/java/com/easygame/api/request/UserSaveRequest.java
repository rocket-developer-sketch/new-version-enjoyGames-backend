package com.easygame.api.request;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveRequest {
    private String nickName;
    private String gameType;

    @Builder
    public UserSaveRequest(String nickName, String gameType) {
        this.nickName = nickName;
        this.gameType = gameType;
    }
}

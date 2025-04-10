package com.easygame.api.request;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveRequest {
    private String nickName;

    @Builder
    public UserSaveRequest(String nickName) {
        this.nickName = nickName;
    }
}

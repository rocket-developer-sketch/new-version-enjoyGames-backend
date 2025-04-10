package com.easygame.api.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveResponse {
    private String token;

    @Builder
    public UserSaveResponse(String token) {
        this.token = token;
    }
}

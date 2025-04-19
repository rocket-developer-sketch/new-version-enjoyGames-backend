package com.easygame.api.response;

import com.easygame.api.security.JwtToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSaveResponse {
    private JwtToken token;

    @Builder
    public UserSaveResponse(JwtToken token) {
        this.token = token;

    }
}

package com.easygame.api.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String code;
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }
}

package com.easygame.api.exception;

import com.easygame.api.response.ErrorCode;

public class InvalidTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InvalidTokenException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_TOKEN;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

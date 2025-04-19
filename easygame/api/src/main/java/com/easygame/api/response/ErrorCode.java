package com.easygame.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST(1, "Invalid request", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(2, "Invalid request", HttpStatus.UNAUTHORIZED),
    TOKEN_RESOLVE_FAILED(3, "Invalid request", HttpStatus.UNAUTHORIZED),
    NOT_FOUND(4, "Invalid request", HttpStatus.NOT_FOUND),
    DUPLICATE_USER(5, "Exist user", HttpStatus.BAD_REQUEST),
    DUPLICATE_SUBMISSION(6, "Invalid Request", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(7, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR);

    private int code;
    private String message;
    private HttpStatus status;
}

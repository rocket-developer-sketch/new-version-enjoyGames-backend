package com.easygame.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST("Invalid Request", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("Illegal Access", HttpStatus.UNAUTHORIZED),
    NOT_FOUND("Not Found", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("Unexpected Error", HttpStatus.INTERNAL_SERVER_ERROR);

  private String message;
  private HttpStatus status;
}

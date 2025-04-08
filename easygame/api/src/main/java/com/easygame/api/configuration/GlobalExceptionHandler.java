package com.easygame.api.configuration;

import com.easygame.api.response.ErrorCode;
import com.easygame.api.response.ErrorResponse;
import com.easygame.service.exception.DuplicateNickNameException;
import com.easygame.api.exception.InvalidTokenException;
import com.easygame.service.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(JsonProcessingException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalJsonRequest(JsonProcessingException e) {
//        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.INVALID_REQUEST));
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegal(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.INVALID_TOKEN));
    }

    @ExceptionHandler(DuplicateNickNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateNickName(DuplicateNickNameException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParam(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(ErrorCode.INTERNAL_ERROR));
    }
}

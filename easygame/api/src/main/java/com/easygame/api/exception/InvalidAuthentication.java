package com.easygame.api.exception;

public class InvalidAuthentication extends RuntimeException {
    public InvalidAuthentication(String message) {
        super(message);
    }
}

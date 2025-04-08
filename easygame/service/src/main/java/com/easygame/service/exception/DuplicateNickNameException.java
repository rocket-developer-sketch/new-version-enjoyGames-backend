package com.easygame.service.exception;

public class DuplicateNickNameException extends RuntimeException {
    public DuplicateNickNameException(String nickname) {
        super("Nickname already exists: " + nickname);
    }
}

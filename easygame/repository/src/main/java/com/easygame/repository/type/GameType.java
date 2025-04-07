package com.easygame.repository.type;

import java.util.Arrays;

public enum GameType {
    NONE,
    COMBAT,
    PIKACHU,
    RABBIT;

    public static GameType from(String value) {
        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException("Game type required");
        }
        
        return Arrays.stream(GameType.values())
                .filter(gt -> gt.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid game type: " + value));
    }

    public static String to(GameType value) {
        return value.name();
    }
}

package com.easygame.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private Long userId;
    private String nickName;

    @Builder
    public UserDto(Long userId, String nickName) {
        this.userId = userId;
        this.nickName = nickName;
    }
}

package com.easygame.service.mapper;

import com.easygame.repository.User;
import com.easygame.service.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements BaseMapper<UserDto, User> {
    @Override
    public UserDto toDto(User entity) {
        return UserDto.builder()
                .userId(entity.getUserId())
                .nickName(entity.getNickName())
                .build();
    }

    @Override
    public User toEntity(UserDto dto) {
        return User.builder()
                .userId(dto.getUserId())
                .nickName(dto.getNickName())
                .build();
    }
}

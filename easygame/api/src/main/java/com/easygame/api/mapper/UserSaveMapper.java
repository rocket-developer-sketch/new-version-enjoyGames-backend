package com.easygame.api.mapper;

import com.easygame.api.request.UserSaveRequest;
import com.easygame.service.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserSaveMapper {
    public UserDto toDto(UserSaveRequest request) {
        return UserDto.builder().nickName(request.getNickName()).build();
    }

}

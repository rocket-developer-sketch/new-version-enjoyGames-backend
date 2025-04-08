package com.easygame.service;

import com.easygame.repository.User;
import com.easygame.repository.UserRepository;
import com.easygame.service.dto.UserDto;
import com.easygame.service.exception.DuplicateNickNameException;
import com.easygame.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto getOrThrow(String nickname) throws Exception {
        return userMapper.toDto(userRepository.findByNickName(nickname)
                .orElseThrow(() -> new Exception("Nickname does not exist"))
        );
    }

    public boolean exists(String nickname) {
        return userRepository.existsByNickName(nickname);
    }

    public UserDto saveUser(UserDto userDto) {
        return userMapper.toDto(userRepository.save(User.builder().nickName(userDto.getNickName()).build()));
    }

    public UserDto registerUser(UserDto userDto) {
        if(exists(userDto.getNickName())) {
            throw new DuplicateNickNameException(userDto.getNickName());
        }

        return saveUser(userDto);
//        return jwtTokenProvider.createToken(newUser.getNickName());
    }

}

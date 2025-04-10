package com.easygame.api.controller;

import com.easygame.api.JwtTokenUtil;
import com.easygame.api.mapper.UserSaveMapper;
import com.easygame.api.request.UserSaveRequest;
import com.easygame.api.response.UserSaveResponse;
import com.easygame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User API", description = "Get nickname and create authentication token")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserSaveMapper userSaveMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Tag(name = "User API")
    @Operation(summary = "Register User", description = "save user's nickname and return token")
    @PostMapping
    public ResponseEntity<UserSaveResponse> registerUser(@RequestBody UserSaveRequest userSaveRequest) throws Exception {
        return ResponseEntity.ok(
                UserSaveResponse.builder()
                        .token(jwtTokenUtil.createTokenWithNickName(userService.registerUser(
                                userSaveMapper.toDto(userSaveRequest)).getNickName())
                        )
                        .build()
        );
    }
}

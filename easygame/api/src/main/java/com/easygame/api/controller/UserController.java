package com.easygame.api.controller;

import com.easygame.api.security.JwtToken;
import com.easygame.api.security.JwtTokenProvider;
import com.easygame.api.security.RedisProvider;
import com.easygame.api.request.UserSaveRequest;
import com.easygame.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User API", description = "Step 1 - user registration and token issuance")
@RequiredArgsConstructor
public class UserController extends BaseController {
    Logger log = LoggerFactory.getLogger(UserController.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisProvider redisProvider;

    @Operation(
        summary = "Get authentication token before starting the game. You can authorize Swagger tests by clicking the 'Authorize' button at the top-right corner and pasting the token",
        description = "Creates and stores user authentication information. This token is required to request a signed token before submitting the game score"
    )
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<JwtToken>> registerUser(@RequestBody UserSaveRequest userSaveRequest) throws Exception {
        JwtToken token = jwtTokenProvider.createToken(userSaveRequest.getGameType(), userSaveRequest.getNickName());

        redisProvider.registerJtiForAuthentication(token.getGameType(), token.getJti(), token.getNickName());

        return ResponseEntity.ok(
                responseSuccess(token)
        );
    }
}

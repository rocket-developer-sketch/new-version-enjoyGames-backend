package com.easygame.api.controller;

import com.easygame.api.exception.InvalidAuthentication;
import com.easygame.api.request.GameScoreSaveTokenRequest;
import com.easygame.api.response.ApiResponse;
import com.easygame.api.response.ErrorCode;
import com.easygame.api.response.GameScoreTokenResponse;
import com.easygame.api.security.CustomUserDetails;
import com.easygame.api.security.GameScoreTokenProvider;
import com.easygame.api.security.JwtTokenProvider;
import com.easygame.api.security.RedisProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/token")
@Tag(name = "Token API", description = "Step 2 - signed token issuance for game score submission")
@RequiredArgsConstructor
public class TokenController extends BaseController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisProvider redisProvider;
    private final GameScoreTokenProvider gameScoreTokenProvider;

    @Operation(
            summary = "Request a signed token before submitting a game score",
            description = "This token is required to validate and authorize the game score submission"
    )
    @PostMapping("/scores")
    public ResponseEntity<ApiResponse<GameScoreTokenResponse>> getToken (
            @RequestHeader(name = "Authorization") String token,
            @RequestBody GameScoreSaveTokenRequest saveTokenRequest) throws InvalidAuthentication {
        CustomUserDetails customUserDetails = getAuthentication();
        String jti = customUserDetails.getUsername();
        String gameType = customUserDetails.getGameType();

        if(!saveTokenRequest.getGameType().equals(gameType) || !saveTokenRequest.getJti().equals(jti)) {
            throw new InvalidAuthentication(ErrorCode.INVALID_REQUEST.getMessage());
        }

        String signedToken = gameScoreTokenProvider.generate(
                customUserDetails.getGameType(),
                customUserDetails.getNickName(),
                saveTokenRequest.getScore(),
                jti
        );

        redisProvider.registerScoreSubmissionJti(customUserDetails.getGameType(), jti, customUserDetails.getNickName());

        return ResponseEntity.ok(responseSuccess(new GameScoreTokenResponse(
                signedToken,
                saveTokenRequest.getScore(),
                jti))
        );
    }

}

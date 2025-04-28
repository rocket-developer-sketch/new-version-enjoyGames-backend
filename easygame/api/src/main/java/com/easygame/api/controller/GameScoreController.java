package com.easygame.api.controller;

import com.easygame.api.mapper.GameScoreSaveMapper;
import com.easygame.api.request.GameScoreSaveRequest;
import com.easygame.api.response.ApiResponse;
import com.easygame.api.security.CustomUserDetails;
import com.easygame.api.security.RedisProvider;
import com.easygame.service.GameScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scores")
@Tag(name = "Game Score API", description = "Step 3 – submit and save the game score")
@RequiredArgsConstructor
public class GameScoreController extends BaseController {
    private final GameScoreService gameScoreService;
    private final GameScoreSaveMapper gameScoreSaveMapper;
    private final RedisProvider redisProvider;

    @Operation(
            summary = "Submit and save the user's game score",
            description = "Validates the authentication token and the signed token before saving the game score securely."
    )
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> saveScore(@RequestBody GameScoreSaveRequest gameScoreSaveRequest,
                                                                   @RequestHeader(name = "Authorization") String token) throws Exception {
            gameScoreService.saveScore(
                    gameScoreSaveMapper.toDto(gameScoreSaveRequest)
            );

        CustomUserDetails customUserDetails = getAuthentication();
        if(customUserDetails != null) {
            String jti = customUserDetails.getUsername();
            redisProvider.deleteJtiForAuthentication(customUserDetails.getGameType(), jti, customUserDetails.getNickName());
        }

        return ResponseEntity.ok(responseSuccess(null));
    }

}

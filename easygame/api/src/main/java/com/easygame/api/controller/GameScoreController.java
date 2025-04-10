package com.easygame.api.controller;

import com.easygame.api.JwtTokenUtil;
import com.easygame.api.RedisUtil;
import com.easygame.api.mapper.GameScoreSaveMapper;
import com.easygame.api.mapper.GameScoreTopMapper;
import com.easygame.api.request.GameScoreSaveRequest;
import com.easygame.api.request.GameScoreTopRequest;
import com.easygame.api.response.GameScoreTopResponse;
import com.easygame.service.GameScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scores")
@Tag(name = "Game Score API", description = "Game Score API")
@RequiredArgsConstructor
public class GameScoreController {
    //private static final Logger log = LoggerFactory.getLogger(GameScoreController.class);

    private final GameScoreService gameScoreService;
    private final GameScoreSaveMapper gameScoreSaveMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisUtil redisUtil;

    @Tag(name = "Game Score API")
    @Operation(summary = "Save User's Game Score", description = "save user's game score and nickname. authorization: Bearer <token>")
    @PostMapping("/user")
    public ResponseEntity<Void> saveScore(@RequestBody GameScoreSaveRequest gameScoreSaveRequest,
                                          @RequestHeader(name = "Authorization") String token) throws Exception {
        if (!redisUtil.isDuplicateSubmission(gameScoreSaveRequest.getNickName(), gameScoreSaveRequest.getGameType())) {
            gameScoreService.saveScore(
                    jwtTokenUtil.getNickNameByResolvedToken(token),
                    gameScoreSaveMapper.toDto(gameScoreSaveRequest)
            );
        }

        return ResponseEntity.ok().build();
    }

}

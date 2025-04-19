package com.easygame.api.controller;

import com.easygame.api.mapper.GameScoreTopMapper;
import com.easygame.api.request.GameScoreTopRequest;
import com.easygame.api.response.GameScoreTopResponse;
import com.easygame.service.GameScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/q/score")
@Tag(name = "Public Query API - Game Score", description = "Handles all GET endpoints related to game scores")
@RequiredArgsConstructor
public class QueryGameScoreController extends BaseController {

    private final GameScoreService gameScoreService;
    private final GameScoreTopMapper gameScoreTopMapper;

    @Operation(
            summary = "Retrieve Top 10 Game Scores",
            description = "Returns the top 10 game scores sorted in descending order."
    )
    @GetMapping("/top")
    public ResponseEntity<List<GameScoreTopResponse>> getTopScores(@RequestParam String gameType, @RequestParam int top) {
        return ResponseEntity.ok(gameScoreService.getTop10ByGameType(
                        gameScoreTopMapper.toDto(GameScoreTopRequest.builder()
                                .gameType(gameType)
                                .top(top)
                                .build()
                        )
                ).stream()
                .map(it -> GameScoreTopResponse.builder()
                        .nickName(it.getNickName())
                        .score(it.getScore())
                        .rank(it.getRank())
                        .gameType(it.getGameTypeStr())
                        .build())
                .toList());
    }


//    @GetMapping("/test")
//    public void testLog() {
//        log.trace("TRACE level message");
//        log.debug("DEBUG level message");
//        log.info("INFO level message");
//        log.warn("WARN level message");
//        log.error("ERROR level message");
//    }

}

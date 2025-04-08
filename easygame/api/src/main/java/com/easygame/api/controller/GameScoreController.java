package com.easygame.api.controller;

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
    private final GameScoreTopMapper gameScoreTopMapper;
    private final GameScoreSaveMapper gameScoreSaveMapper;

    @Tag(name = "Game Score API")
    @Operation(summary = "Save User's Game Score", description = "save user's game score and nickname. authorization: Bearer <token>")
    @PostMapping
    public ResponseEntity<Void> saveScore(@RequestBody GameScoreSaveRequest gameScoreSaveRequest, @RequestHeader(name = "Authorization") String token) throws Exception {
        gameScoreService.saveScoreWithJwt(token, gameScoreSaveMapper.toDto(gameScoreSaveRequest));
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Game Score API")
    @Operation(summary = "Show Top 10", description = "query top 10")
    @GetMapping("/top")
    public ResponseEntity<List<GameScoreTopResponse>> getTopScores(@RequestParam String gameType, @RequestParam int top) {
        return ResponseEntity.ok(gameScoreService.getTop10ByGameType(gameScoreTopMapper.toDto(GameScoreTopRequest.builder()
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

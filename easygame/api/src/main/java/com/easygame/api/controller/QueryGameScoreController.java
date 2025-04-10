package com.easygame.api.controller;

import com.easygame.api.mapper.GameScoreTopMapper;
import com.easygame.api.request.GameScoreTopRequest;
import com.easygame.api.response.GameScoreTopResponse;
import com.easygame.service.GameScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/q/score")
@Tag(name = "Game Score API", description = "Game Score API")
@RequiredArgsConstructor
public class QueryGameScoreController {
    private final GameScoreService gameScoreService;
    private final GameScoreTopMapper gameScoreTopMapper;

    @Tag(name = "Game Score API")
    @Operation(summary = "Show Top 10", description = "query top 10")
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

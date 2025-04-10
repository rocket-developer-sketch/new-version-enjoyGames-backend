package com.easygame.service;

import com.easygame.repository.type.GameType;
import com.easygame.service.dto.GameScoreDto;
import com.easygame.service.dto.UserDto;
import com.easygame.repository.GameScore;
import com.easygame.repository.GameScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class GameScoreService {
    private final GameScoreRepository gameScoreRepository;
    private final UserService userService;


    public void saveScore(String resolvedNickName, GameScoreDto gameScoreDto) throws Exception {
        if(!isValidRangeOfScore(gameScoreDto.getScore())) {
            throw new IllegalArgumentException("Invalid score range");
        }

        UserDto userDto = validateScoreSubmission(resolvedNickName, gameScoreDto);

        gameScoreRepository.save(GameScore.builder()
                .userId(userDto.getUserId())
                .score(gameScoreDto.getScore())
                .gameType(GameType.from(gameScoreDto.getGameTypeStr()))
                .build()
        );
    }

    public List<GameScoreDto> getTop10ByGameType(GameScoreDto gameScoreDto) {
        if(!isValidRangeOfTopScore(gameScoreDto.getTop())) {
            throw new IllegalArgumentException("invalid range");
        }

        return getRankedScores(gameScoreRepository.findByGameType(gameScoreDto.getGameType(),
                        PageRequest.of(0, gameScoreDto.getTop(), Sort.by(Sort.Direction.DESC, "score"))
                )
        );
    }

    private UserDto validateScoreSubmission(String resolvedNickName, GameScoreDto gameScoreDto) throws Exception {
        UserDto userDto = userService.getOrThrow(resolvedNickName);

        if(!userDto.getNickName().equals(gameScoreDto.getNickName())) {
            throw new IllegalArgumentException("illegal access user");
        }

        return userDto;
    }

    private boolean isValidRangeOfTopScore(int top) {
        return top > 0 && 1000 > top;
    }

    private boolean isValidRangeOfScore(int score) {
        return score >= 0;
    }

    // When multiple players have the same score, the next rank is incremented accordingly.
    // can do with AtomicInteger
    private List<GameScoreDto> getRankedScores(List<GameScore> scores) {
        List<GameScoreDto> rankedScores = new ArrayList<>();

        int rank = 1;
        int prevScore = -1;

        for (int i = 0; i < scores.size(); i++) {
            GameScore score = scores.get(i);
            int currentScore = score.getScore();

            if (i > 0 && currentScore != prevScore) {
                rank++;
            }

            rankedScores.add(GameScoreDto.builder()
                    .rank(rank)
                    .nickName(score.getUser().getNickName())
                    .score(currentScore)
                    .gameTypeStr(score.getGameType().name())
                    .build());

            prevScore = currentScore;
        }

        return rankedScores;
    }
}

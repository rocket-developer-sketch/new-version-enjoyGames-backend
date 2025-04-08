package com.easygame.service;

import com.easygame.repository.type.GameType;
import com.easygame.service.dto.GameScoreDto;
import com.easygame.service.dto.UserDto;
import com.easygame.service.exception.InvalidTokenException;
import com.easygame.service.mapper.GameScoreMapper;
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
    private final GameScoreMapper gameScoreMapper;
    private final GameScoreRepository gameScoreRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public void saveScoreWithJwt(String headerToken, GameScoreDto gameScoreDto) throws Exception {
        String token = resolveToken(headerToken);

        if (!jwtTokenProvider.isValidToken(token)) {
            throw new InvalidTokenException("invalid token");
        }

        String nickname = jwtTokenProvider.getNickname(token);
        UserDto userDto = userService.getOrThrow(nickname);

        if(!userDto.getNickName().equals(gameScoreDto.getNickName())) {
            throw new IllegalArgumentException("illegal access user");
        }

        gameScoreRepository.save(GameScore.builder()
                .userId(userDto.getUserId())
                .score(gameScoreDto.getScore())
                .gameType(GameType.from(gameScoreDto.getGameTypeStr()))
                .build()
        );
    }

    private String resolveToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new InvalidTokenException("cannot resolve token");
    }
    public List<GameScoreDto> getTop10ByGameType(GameScoreDto gameScoreDto) {
        if(gameScoreDto.getTop() <= 0 || 1000 <= gameScoreDto.getTop()) {
            throw new IllegalArgumentException("invalid range");
        }


        return getRankedScores(gameScoreRepository.findByGameType(gameScoreDto.getGameType(),
                        PageRequest.of(0, gameScoreDto.getTop(), Sort.by(Sort.Direction.DESC, "score"))
                )
        );
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

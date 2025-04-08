package com.easygame.service;

import com.easygame.repository.type.GameType;
import com.easygame.service.dto.GameScoreDto;
import com.easygame.service.dto.UserDto;
import com.easygame.service.exception.InvalidTokenException;
import com.easygame.service.exception.NotFoundException;
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

    private List<GameScoreDto> getRankedScores(List<GameScore> scores) {
        List<GameScoreDto> rankedScores = new ArrayList<>();
        int rank = 0;
        int trackRank = 0;
        Integer prevScore = null;

        for (GameScore score : scores) {
            trackRank++;

            if (prevScore == null || score.getScore() != prevScore) {
                rank = trackRank;
            }

            rankedScores.add(GameScoreDto.builder()
                    .top(rank)
                    .nickName(score.getUser().getNickName())
                    .score(score.getScore())
                    .gameTypeStr(score.getGameType().name())
                    .build());

            prevScore = score.getScore();
        }

        return rankedScores;
    }
}

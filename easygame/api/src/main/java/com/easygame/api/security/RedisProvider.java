package com.easygame.api.security;

import com.easygame.api.configuration.RedisCustomProperties;
import com.easygame.api.exception.DuplicateSubmissionException;
import com.easygame.api.exception.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@AllArgsConstructor
public class RedisProvider {
    private final RedisCustomProperties redisProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public String registerJtiForAuthentication(String gameType, String jti, String nickName) {
        String redisKey = makeAuthKey(gameType, jti, URLEncoder.encode(nickName, StandardCharsets.UTF_8));

        redisTemplate.opsForValue().set(redisKey, "1", Duration.ofMinutes(redisProperties.getTtls().getWhitelistTtl()));
        return jti;
    }

    public String registerScoreSubmissionJti(String gameType, String jti, String nickName) {
        String redisKey = makeSubmitKey(gameType, jti, URLEncoder.encode(nickName, StandardCharsets.UTF_8));

        redisTemplate.opsForValue().set(redisKey, "1", Duration.ofMinutes(redisProperties.getTtls().getSubmitScoreTtl()));
        return jti;
    }

    public void deleteJtiForAuthentication(String gameType, String jti, String nickName) throws DuplicateSubmissionException {
        String redisKey = makeAuthKey(gameType, jti, URLEncoder.encode(nickName, StandardCharsets.UTF_8));

        if (Boolean.FALSE.equals(redisTemplate.delete(redisKey))) {
            throw new DuplicateSubmissionException(String.format(
                    "Fail to delete jti for authentication. (gameType: %s, jti: %s, nickName: %s)", gameType, jti, nickName));
        }
    }

    public void deleteJtiForScoreSubmission(String gameType, String jti, String nickName) throws DuplicateSubmissionException {
        String redisKey = makeSubmitKey(gameType, jti, URLEncoder.encode(nickName, StandardCharsets.UTF_8));

        if (Boolean.FALSE.equals(redisTemplate.delete(redisKey))) {
            throw new DuplicateSubmissionException(String.format(
                    "Fail to delete jti for score submission. (gameType: %s, jti: %s, nickName: %s)", gameType, jti, nickName));
        }
    }

    public void isRegisteredAuthenticationJti(String gameType, String nickName, String jti) throws InvalidTokenException {
        String redisKey = makeAuthKey(gameType, jti, URLEncoder.encode(nickName, StandardCharsets.UTF_8));

        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            throw new InvalidTokenException("Cannot find token");
        }
    }

    private String makeAuthKey(String gameType, String jti, String nickName) {
        return String.format("%s:%s:%s:%s", redisProperties.getKeys().getWhitelistPrefix(), gameType, jti, nickName);
    }

    private String makeSubmitKey(String gameType, String jti, String nickName) {
        return String.format("%s:%s:%s:%s", redisProperties.getKeys().getSubmitScorePrefix(), gameType, jti, nickName);
    }

}

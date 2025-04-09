package com.easygame.api;

import com.easygame.api.configuration.RedisCustomProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
@Component
public class RedisProvider {
    private final RedisCustomProperties redisProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean isFirstWithinTTL(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, "locked",
                Duration.ofMinutes(redisProperties.getTtlMinutes())
        );
    }
}

package com.easygame.api.integration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@Profile("test")
public class EmbeddedRedisConfig {
    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServer(6379); // 충돌 시 포트 변경 가능
        redisServer.start();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ✅ Embedded Redis started on port 6379");
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}

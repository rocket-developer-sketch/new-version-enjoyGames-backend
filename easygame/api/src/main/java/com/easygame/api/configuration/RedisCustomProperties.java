package com.easygame.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "redis")
public class RedisCustomProperties {
    private RedisKeys keys;
    private TTLs ttls;

    @Getter
    @Setter
    public static class RedisKeys {
        private String whitelistPrefix;
        private String submitScorePrefix;
    }

    @Getter
    @Setter
    public static class TTLs {
        private int whitelistTtl;
        private int submitScoreTtl;
    }
}

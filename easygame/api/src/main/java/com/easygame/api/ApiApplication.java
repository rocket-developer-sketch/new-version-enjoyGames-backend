package com.easygame.api;

import com.easygame.api.configuration.FilterPathsProperties;
import com.easygame.api.configuration.HmacProperties;
import com.easygame.api.configuration.JwtProperties;
import com.easygame.api.configuration.RedisCustomProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = "com.easygame")
@EnableJpaRepositories(basePackages = "com.easygame.repository")
@EntityScan(basePackages = "com.easygame.repository")
@EnableConfigurationProperties({
        JwtProperties.class, RedisCustomProperties.class,
        HmacProperties.class, FilterPathsProperties.class})
@SpringBootApplication
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}

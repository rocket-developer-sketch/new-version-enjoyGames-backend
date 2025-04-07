package com.easygame.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class JacksonConfig {
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void registerXssModule() {
        objectMapper.registerModule(new XssModule());
    }

    @Primary  // escape text in production environment
    @Bean
    public ObjectMapper xssProtectedObjectMapper() {
        objectMapper.registerModule(new XssModule());
        return objectMapper;
    }
}

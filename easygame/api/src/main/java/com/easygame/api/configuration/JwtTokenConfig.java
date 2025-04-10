//package com.easygame.api.configuration;
//
//import com.easygame.api.JwtTokenProvider;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableConfigurationProperties(JwtProperties.class)
//public class JwtTokenConfig {
//    @Bean
//    public JwtTokenProvider jwtTokenProvider(JwtProperties jwtProperties) {
//        return JwtTokenProvider.builder().secretKey(jwtProperties.getSecretKey())
//                    .expirationMs(jwtProperties.getExpirationMs())
//                .build();
//    }
//}

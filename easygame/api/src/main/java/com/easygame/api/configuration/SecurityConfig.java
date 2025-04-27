package com.easygame.api.configuration;

import com.easygame.api.response.ErrorCode;
import com.easygame.api.security.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisProvider redisProvider;
    private final GameScoreTokenProvider gameScoreTokenProvider;
    private final FilterPathsProperties filterPathsProperties;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, RedisProvider redisProvider,
                          GameScoreTokenProvider gameScoreTokenProvider, FilterPathsProperties filterPathsProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisProvider = redisProvider;
        this.gameScoreTokenProvider = gameScoreTokenProvider;
        this.filterPathsProperties = filterPathsProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/scores/**").authenticated()
                        .requestMatchers("/api/v1/token/scores").authenticated()
                        .anyRequest().permitAll() // Authentication is handled directly in the filter for all incoming requests
                )
                .addFilterBefore(new RequestResponseLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisProvider, filterPathsProperties),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtScoreValidationFilter(
                        jwtTokenProvider, redisProvider,
                        gameScoreTokenProvider, filterPathsProperties
                ), JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        //config.addAllowedOrigin("*"); // allowed every origins while developing
        config.setAllowedOrigins(Arrays.asList("http://localhost:8081","http://localhost:5173"));
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}

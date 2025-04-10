package com.easygame.api.configuration;

import com.easygame.api.JwtAuthenticationFilter;
import com.easygame.api.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthProperties authProperties;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil, AuthProperties authProperties) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authProperties = authProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/scores/**").authenticated()
                        .anyRequest().permitAll() // Authentication is handled directly in the filter for all incoming requests
                ).addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, authProperties),
                        UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}

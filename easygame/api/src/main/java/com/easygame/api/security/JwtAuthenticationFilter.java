package com.easygame.api.security;

import com.easygame.api.configuration.FilterPathsProperties;
import com.easygame.api.response.ErrorCode;
import com.easygame.api.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisProvider redisProvider;
    private final FilterPathsProperties filterPathsProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RedisProvider redisProvider,
                                   FilterPathsProperties filterPathsProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisProvider = redisProvider;
        this.filterPathsProperties = filterPathsProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        boolean isIncluded = filterPathsProperties.getAuthPath().getIncludePaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));

        if (!isIncluded) {
            filterChain.doFilter(request, response); // no authentication needed
            return;
        }

        String header = request.getHeader("Authorization");

        try {
            String jti = jwtTokenProvider.getJti(header);
            String nickName = jwtTokenProvider.getNickName(header);
            String gameType = jwtTokenProvider.getGameType(header);

            redisProvider.isRegisteredAuthenticationJti(gameType, nickName, jti);

            Authentication auth = jwtTokenProvider.getAuthentication(gameType, jti, nickName);

            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("API Authentication Exception : {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(ErrorCode.INVALID_TOKEN));
        }
    }
}

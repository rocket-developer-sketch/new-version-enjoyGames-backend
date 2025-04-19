package com.easygame.api.security;

import com.easygame.api.configuration.FilterPathsProperties;
import com.easygame.api.exception.InvalidAuthentication;
import com.easygame.api.request.GameScoreSaveRequest;
import com.easygame.api.response.ErrorCode;
import com.easygame.api.response.ErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JwtScoreValidationFilter  extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtScoreValidationFilter.class);
    
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisProvider redisProvider;
    private final GameScoreTokenProvider gameScoreTokenProvider;
    private final FilterPathsProperties filterPathsProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final long MAX_SIZE_BYTES = 1024 * 1024 * 2; // 2MB

    public JwtScoreValidationFilter(JwtTokenProvider jwtTokenProvider, RedisProvider redisProvider,
                                    GameScoreTokenProvider gameScoreTokenProvider,
                                    FilterPathsProperties filterPathsProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisProvider = redisProvider;
        this.gameScoreTokenProvider = gameScoreTokenProvider;
        this.filterPathsProperties = filterPathsProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        boolean isIncluded = filterPathsProperties.getSubmitPath().getIncludePaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));

        if (!isIncluded) {
            filterChain.doFilter(request, response); // no authentication needed
            return;
        }

        long contentLength = request.getContentLengthLong(); 
        if (contentLength != -1 && contentLength > MAX_SIZE_BYTES) { // CustomBodyRequestWrapper 한 번에 많은 용량 메모리에 올리기 방지
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Payload too large");
            return;
        }

        // Apply wrapper to cache the request body for validation,
        // then pass the wrapped request along the filter chain to ensure the controller can still access the body.
        CustomBodyRequestWrapper wrappedRequest = new CustomBodyRequestWrapper(request);

        // body parsing
        String requestBody = new String(wrappedRequest.getBody(), StandardCharsets.UTF_8);

        try {

            JsonNode rootJson = objectMapper.readTree(requestBody);

            int score = rootJson.get("score").asInt();
            String jti = rootJson.get("jti").asText();
            String gameType = rootJson.get("gameType").asText();
            String signedToken = rootJson.get("signedToken").asText();

            CustomUserDetails authentication = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if(!authentication.getUsername().equals(jti)) {
                throw new InvalidAuthentication("Jti is not authenticated");
            }

            String nickName = authentication.getNickName();
            gameScoreTokenProvider.isValidSignedToken(gameType, nickName, score, jti, signedToken);

            redisProvider.deleteJtiForScoreSubmission(gameType, jti, nickName); // remove token from the whitelist to prevent duplicate requests

            filterChain.doFilter(wrappedRequest, response);
        } catch (Exception e) {
            log.error("API Score Token Submission Exception : {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(ErrorCode.INVALID_TOKEN));
        }
    }


    /**
     * // Inner class: Wraps the request to make the body reusable across filters and controllers
     */
    private static class CustomBodyRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] body;

        public CustomBodyRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.body = request.getInputStream().readAllBytes();
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(body);

            return new ServletInputStream() {
                @Override public boolean isFinished() { return bais.available() == 0; }
                @Override public boolean isReady() { return true; }
                @Override public void setReadListener(ReadListener readListener) { }
                @Override public int read() throws IOException { return bais.read(); }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }

        public byte[] getBody() {
            return body;
        }
    }

}

package com.easygame.api.security;

import com.easygame.api.configuration.FilterPathsProperties;
import com.easygame.api.exception.InvalidTokenException;
import com.easygame.api.response.ErrorCode;
import com.easygame.api.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class JwtScoreValidationFilterTest {

    private JwtScoreValidationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String scoreSubmissionURI = "/api/v1/scores";
    private TestAuthHelper authHelper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisProvider redisProvider;

    @Mock
    private GameScoreTokenProvider gameScoreTokenProvider;

    private FilterPathsProperties filterPathsProperties;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        MockitoAnnotations.openMocks(this);

        // URI 패턴 포함 설정
        filterPathsProperties = new FilterPathsProperties();
        FilterPathsProperties.SubmitPath submitPath = new FilterPathsProperties.SubmitPath();
        submitPath.setIncludePaths(List.of(scoreSubmissionURI));
        filterPathsProperties.setSubmitPath(submitPath);

        filter = new JwtScoreValidationFilter(jwtTokenProvider, redisProvider, gameScoreTokenProvider, filterPathsProperties);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        authHelper = new TestAuthHelper(jwtTokenProvider, gameScoreTokenProvider);

        String gameType = "PIKACHU";
        when(jwtTokenProvider.createToken(eq(gameType), anyString()))
                .thenAnswer(invocation -> {
                    String nickname = invocation.getArgument(0);
                    return new JwtToken(gameType, "access-token-for-" + nickname, nickname + "-jti", nickname);
                });

        when(jwtTokenProvider.getJti(startsWith("access-token-for-")))
                .thenAnswer(invocation -> {
                    String accessToken = invocation.getArgument(0);
                    return accessToken.replace("access-token-for-", "") + "-jti";
                });

    }

    @DisplayName("Should allow request when token is valid")
    @Test
    void validRequest_shouldPassFilter() throws Exception {
        String nickname = "test1";
        String gameType = "PIKACHU";
        String signedToken = "valid-signed-token";
        int score = 1000;

        // set SecurityContext
        authHelper.authenticate(gameType, nickname);
        String jti = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // create request body
        String body = authHelper.makeRequestBody(gameType, score, jti, signedToken);

        setupRequest(body);

        // Token validation succeeded
        doNothing().when(gameScoreTokenProvider).isValidSignedToken(gameType, nickname, score, jti, signedToken);

        filter.doFilter(request, response, filterChain);

        verify(gameScoreTokenProvider).isValidSignedToken(eq(gameType), any(), anyInt(), any(), any());
        verify(redisProvider).deleteJtiForScoreSubmission(gameType, jti, nickname);
        // When an exception occurs during Redis deletion
        doThrow(new RuntimeException("Redis deletion failed")).when(redisProvider).deleteJtiForScoreSubmission(eq(gameType), any(), any());
        verify(filterChain).doFilter(any(HttpServletRequest.class), eq(response)); // the filter uses custom HttpServletRequest
    }

    @DisplayName("Should return error response when signedToken is missing")
    @Test
    void signedTokenMissing_shouldReturnUnauthorized() throws Exception {
        String nickname = "test1";
        String gameType = "PIKACHU";
        authHelper.authenticate(gameType, nickname);

        String jti = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        String body = """
            {
              "score": 1000,
              "jti": "%s"
            }
        """.formatted(jti);

        setupRequest(body);

        filter.doFilter(request, response, filterChain);
        verify(filterChain, never()).doFilter(any(), any());

        ErrorResponse error = objectMapper.readValue(response.getContentAsByteArray(), ErrorResponse.class);
        assertEquals(ErrorCode.INVALID_TOKEN.getCode(), error.getCode());
    }

    @DisplayName("Should return error response when jti does not match authenticated user")
    @Test
    void jtiMismatch_shouldReturnUnauthorized() throws Exception {
        String nickname = "test1";
        String gameType = "PIKACHU";
        authHelper.authenticate(gameType, nickname);

        String wrongJti = "test1-wrong-jti";
        String body = authHelper.makeRequestBody(gameType, 1000, wrongJti, "valid-signed-token");
        setupRequest(body);

        filter.doFilter(request, response, filterChain);

        ErrorResponse error = objectMapper.readValue(response.getContentAsByteArray(), ErrorResponse.class);
        assertEquals(ErrorCode.INVALID_TOKEN.getCode(), error.getCode());
        assertEquals(ErrorCode.INVALID_TOKEN.getMessage(), error.getMessage());

        verify(filterChain, never()).doFilter(any(), any());
        verifyNoInteractions(gameScoreTokenProvider);
    }

    @DisplayName("Should return error response when signedToken is tampered")
    @Test
    void invalidSignedToken_shouldReturnUnauthorized() throws Exception {
        // 목적: 조작된 signedToken이 들어왔을 때 필터가 401을 반환하는지 테스트
        String nickname = "test1";
        String gameType = "PIKACHU";
        authHelper.authenticate(gameType, nickname);

        String jti = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        int score = 1000;
        String tamperedToken = "invalid-token-signature"; // 잘못된 서명

        String body = authHelper.makeRequestBody(gameType, score, jti, tamperedToken);
        setupRequest(body);

        doThrow(new InvalidTokenException(ErrorCode.INVALID_TOKEN))
                .when(gameScoreTokenProvider).isValidSignedToken(gameType, nickname, score, jti, tamperedToken);

        filter.doFilter(request, response, filterChain);

        ErrorResponse error = objectMapper.readValue(response.getContentAsByteArray(), ErrorResponse.class);
        assertEquals(ErrorCode.INVALID_TOKEN.getCode(), error.getCode());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(redisProvider, never()).deleteJtiForScoreSubmission(eq(gameType), any(), any());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @DisplayName("Should return error response for malformed JSON request")
    @Test
    void malformedJson_shouldReturnBadRequest() throws Exception {
        request.setRequestURI(scoreSubmissionURI);
        request.setMethod("POST");
        request.setContent("{ invalid json }".getBytes(StandardCharsets.UTF_8));
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);

        filter.doFilter(request, response, filterChain);

        ErrorResponse error = objectMapper.readValue(response.getContentAsByteArray(), ErrorResponse.class);
        assertEquals(ErrorCode.INVALID_TOKEN.getCode(), error.getCode());
    }

    @DisplayName("Should return error response when gameType is missing in token or request")
    @Test
    void missingGameType_shouldReturnUnauthorized() throws Exception {
        String nickname = "test1";
        String gameType = "PIKACHU";
        authHelper.authenticate(gameType, nickname);

        String jti = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        String signedToken = "valid-token";
        String body = authHelper.makeRequestBody(null, 1000, jti, signedToken);
        setupRequest(body);

        filter.doFilter(request, response, filterChain);

        ErrorResponse error = objectMapper.readValue(response.getContentAsByteArray(), ErrorResponse.class);
        assertEquals(ErrorCode.INVALID_TOKEN.getCode(), error.getCode());
    }

    private void setupRequest(String body) {
        request.setRequestURI(scoreSubmissionURI);
        request.setMethod("POST");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

}

package com.easygame.api.integration;

import com.easygame.api.request.GameScoreSaveRequest;
import com.easygame.api.request.GameScoreSaveTokenRequest;
import com.easygame.api.request.UserSaveRequest;
import com.easygame.api.response.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * execute command:
 * ./gradlew :api:test --tests "com.easygame.api.integration.ScoreSubmissionFlowTest"
 * */

@SpringBootTest
@Import({EmbeddedRedisConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
public class ScoreSubmissionFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Success: Complete score submission flow")
    @Test
    void fullFlow_shouldSucceed() throws Exception {
        String nickName = "testplayer1";
        String gameType = "PIKACHU";
        int score = 150;

        // 1. 사용자 생성 → JWT 발급
        UserSaveRequest userReq = new UserSaveRequest(nickName, gameType);
        String userBody = objectMapper.writeValueAsString(userReq);

        MvcResult userRes = mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.token");
        String jti = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.jti");

        // 2. HMAC 토큰 생성
        GameScoreSaveTokenRequest tokenReq = new GameScoreSaveTokenRequest(gameType, jti, score);
        String tokenBody = objectMapper.writeValueAsString(tokenReq);

        MvcResult tokenRes = mockMvc.perform(post("/api/v1/token/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenBody))
                .andExpect(status().isOk())
                .andReturn();

        String signedToken = JsonPath.read(tokenRes.getResponse().getContentAsString(), "$.data.signedToken");

        // 3. 실제 점수 저장
        GameScoreSaveRequest scoreReq = new GameScoreSaveRequest(gameType, nickName, score, jti, signedToken);
        String scoreBody = objectMapper.writeValueAsString(scoreReq);

        mockMvc.perform(post("/api/v1/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scoreBody))
                .andExpect(status().isOk());
    }

    @DisplayName("Failure: Tampered signedToken during score submission")
    @Test
    void fullFlow_shouldFail_onTamperedSignedToken() throws Exception {
        String nickName = "testplayer1";
        String gameType = "PIKACHU";
        int score = 150;

        // 1. 사용자 생성 → JWT 발급
        UserSaveRequest userReq = new UserSaveRequest(nickName, gameType);
        String userBody = objectMapper.writeValueAsString(userReq);

        MvcResult userRes = mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.token");
        String jti = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.jti");

        // 2. HMAC 토큰 생성 (정상 토큰 생성)
        GameScoreSaveTokenRequest tokenReq = new GameScoreSaveTokenRequest(gameType, jti, score);
        String tokenBody = objectMapper.writeValueAsString(tokenReq);

        MvcResult tokenRes = mockMvc.perform(post("/api/v1/token/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenBody))
                .andExpect(status().isOk())
                .andReturn();

        String validSignedToken = JsonPath.read(tokenRes.getResponse().getContentAsString(), "$.data.signedToken");

        // 3. signedToken 조작
        String tamperedSignedToken = validSignedToken.substring(0, validSignedToken.length() - 1) + "X";

        // 4. 점수 저장 요청
        GameScoreSaveRequest scoreReq = new GameScoreSaveRequest(gameType, nickName, score, jti, tamperedSignedToken);
        String scoreBody = objectMapper.writeValueAsString(scoreReq);

        mockMvc.perform(post("/api/v1/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scoreBody))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()));
    }

    @DisplayName("Failure: Tampered score value during submission")
    @Test
    void fullFlow_shouldFail_onTamperedScore() throws Exception {
        String nickName = "testplayer1";
        String gameType = "PIKACHU";
        int originalScore = 150;

        // 1. 사용자 생성 → JWT 발급
        UserSaveRequest userReq = new UserSaveRequest(nickName, gameType);
        String userBody = objectMapper.writeValueAsString(userReq);

        MvcResult userRes = mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.token");
        String jti = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.jti");

        // 2. 서명 토큰 생성 (정상 score 기준)
        GameScoreSaveTokenRequest tokenReq = new GameScoreSaveTokenRequest(gameType, jti, originalScore);
        String tokenBody = objectMapper.writeValueAsString(tokenReq);

        MvcResult tokenRes = mockMvc.perform(post("/api/v1/token/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenBody))
                .andExpect(status().isOk())
                .andReturn();

        String signedToken = JsonPath.read(tokenRes.getResponse().getContentAsString(), "$.data.signedToken");

        // 3. 실제 점수 저장 (score만 조작)
        int tamperedScore = 99999; // 원래보다 높은 점수
        GameScoreSaveRequest scoreReq = new GameScoreSaveRequest(gameType, nickName, tamperedScore, jti, signedToken);
        String scoreBody = objectMapper.writeValueAsString(scoreReq);

        mockMvc.perform(post("/api/v1/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scoreBody))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()));
    }

    @DisplayName("Failure: Tampered jti value during submission")
    @Test
    void fullFlow_shouldFail_onTamperedJti() throws Exception {
        String nickName = "testplayer1";
        String gameType = "PIKACHU";
        int score = 150;

        // 1. 사용자 생성 → JWT 발급
        UserSaveRequest userReq = new UserSaveRequest(nickName, gameType);
        String userBody = objectMapper.writeValueAsString(userReq);

        MvcResult userRes = mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.token");
        String originalJti = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.jti");

        // 2. HMAC 서명 생성
        GameScoreSaveTokenRequest tokenReq = new GameScoreSaveTokenRequest(gameType, originalJti, score);
        String tokenBody = objectMapper.writeValueAsString(tokenReq);

        MvcResult tokenRes = mockMvc.perform(post("/api/v1/token/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenBody))
                .andExpect(status().isOk())
                .andReturn();

        String signedToken = JsonPath.read(tokenRes.getResponse().getContentAsString(), "$.data.signedToken");

        // 3. 점수 저장 요청 (jti만 위조)
        String tamperedJti = "fake-jti-123";
        GameScoreSaveRequest scoreReq = new GameScoreSaveRequest(gameType, nickName, score, tamperedJti, signedToken);
        String scoreBody = objectMapper.writeValueAsString(scoreReq);

        mockMvc.perform(post("/api/v1/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scoreBody))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Failure: Duplicate submission with the same jti")
    @Test
    void fullFlow_shouldFail_onDuplicateSubmission() throws Exception {
        String nickName = "testplayer1";
        String gameType = "PIKACHU";
        int score = 150;

        // 1. 사용자 생성 → JWT 발급
        UserSaveRequest userReq = new UserSaveRequest(nickName, gameType);
        String userBody = objectMapper.writeValueAsString(userReq);

        MvcResult userRes = mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.token");
        String jti = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.jti");

        // 2. HMAC 토큰 생성
        GameScoreSaveTokenRequest tokenReq = new GameScoreSaveTokenRequest(gameType, jti, score);
        String tokenBody = objectMapper.writeValueAsString(tokenReq);

        MvcResult tokenRes = mockMvc.perform(post("/api/v1/token/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenBody))
                .andExpect(status().isOk())
                .andReturn();

        String signedToken = JsonPath.read(tokenRes.getResponse().getContentAsString(), "$.data.signedToken");

        // 3. 첫 점수 제출 → 성공
        GameScoreSaveRequest scoreReq = new GameScoreSaveRequest(gameType, nickName, score, jti, signedToken);
        String scoreBody = objectMapper.writeValueAsString(scoreReq);

        mockMvc.perform(post("/api/v1/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scoreBody))
                .andExpect(status().isOk());

        // 4. 동일 jti로 다시 제출 → 실패해야 함
        mockMvc.perform(post("/api/v1/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scoreBody))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()));
    }

    @DisplayName("Failure: Different score submitted with the same jti (HMAC mismatch)")
    @Test
    void fullFlow_shouldFail_onDifferentScoreWithSameJti() throws Exception {
        String nickName = "testplayer1";
        String gameType = "PIKACHU";
        int originalScore = 150;
        int tamperedScore = 9999;

        // 1. 사용자 생성 → JWT 발급
        UserSaveRequest userReq = new UserSaveRequest(nickName, gameType);
        String userBody = objectMapper.writeValueAsString(userReq);

        MvcResult userRes = mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.token");
        String jti = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.jti");

        // 2. HMAC 토큰 생성 (원래 점수로)
        GameScoreSaveTokenRequest tokenReq = new GameScoreSaveTokenRequest(gameType, jti, originalScore);
        String tokenBody = objectMapper.writeValueAsString(tokenReq);

        MvcResult tokenRes = mockMvc.perform(post("/api/v1/token/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenBody))
                .andExpect(status().isOk())
                .andReturn();

        String signedToken = JsonPath.read(tokenRes.getResponse().getContentAsString(), "$.data.signedToken");

        // 3. 같은 jti, 다른 점수로 제출 (서명은 이전 것이므로 검증 실패해야 함)
        GameScoreSaveRequest tamperedScoreReq = new GameScoreSaveRequest(gameType, nickName, tamperedScore, jti, signedToken);
        String tamperedBody = objectMapper.writeValueAsString(tamperedScoreReq);

        mockMvc.perform(post("/api/v1/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tamperedBody))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()));
    }

    @DisplayName("Failure: gameType mismatch between JWT and HMAC token request")
    @Test
    void fullFlow_shouldFail_onGameTypeMismatchBetweenJwtAndTokenRequest() throws Exception {
        String nickName = "testplayer1";
        String jwtGameType = "PIKACHU";     // JWT 발급 시
        String tamperedGameType = "RABBIT"; // HMAC 요청 시 위조

        int score = 150;

        // 1. 사용자 생성 → JWT 발급
        UserSaveRequest userReq = new UserSaveRequest(nickName, jwtGameType);
        String userBody = objectMapper.writeValueAsString(userReq);

        MvcResult userRes = mockMvc.perform(post("/api/v1/user/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.token");
        String jti = JsonPath.read(userRes.getResponse().getContentAsString(), "$.data.jti");

        // 2. 위조된 gameType으로 HMAC 요청
        GameScoreSaveTokenRequest tokenReq = new GameScoreSaveTokenRequest(tamperedGameType, jti, score);
        String tokenBody = objectMapper.writeValueAsString(tokenReq);

        mockMvc.perform(post("/api/v1/token/scores")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tokenBody))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_REQUEST.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_REQUEST.getCode()));
    }
}

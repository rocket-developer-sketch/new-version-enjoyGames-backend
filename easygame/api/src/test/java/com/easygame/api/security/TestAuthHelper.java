package com.easygame.api.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestAuthHelper {

    private final JwtTokenProvider jwtTokenProvider;
    private final GameScoreTokenProvider gameScoreTokenProvider;

    public TestAuthHelper(JwtTokenProvider jwtTokenProvider,
                          GameScoreTokenProvider gameScoreTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.gameScoreTokenProvider = gameScoreTokenProvider;
    }

    public void authenticate(String gameType, String nickname) {
        JwtToken jwtToken = jwtTokenProvider.createToken(gameType, nickname);
        if (jwtToken == null) {
            throw new IllegalStateException("jwtTokenProvider.createToken(nickname) returned null");
        }

        String jti = jwtToken.getJti();

        CustomUserDetails userDetails = new CustomUserDetails(gameType, jti, nickname);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public String getJti(String gameType, String nickname) {
        JwtToken token = jwtTokenProvider.createToken(gameType, nickname);
        return jwtTokenProvider.getJti(token.getJti());
    }

    public String generateSignedToken(String gameType, String nickname, int score, String jti) {
        return gameScoreTokenProvider.generate(gameType, nickname, score, jti);
    }

    public String makeRequestBody(String gameType, int score, String jti, String signedToken) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();

        if (gameType != null) node.put("gameType", gameType);
        node.put("score", score);
        node.put("jti", jti);
        if (signedToken != null) node.put("signedToken", signedToken);

        return objectMapper.writeValueAsString(node);
    }
}

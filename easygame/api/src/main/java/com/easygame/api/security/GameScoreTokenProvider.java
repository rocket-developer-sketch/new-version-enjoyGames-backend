package com.easygame.api.security;

import com.easygame.api.configuration.HmacProperties;
import com.easygame.api.exception.InvalidTokenException;
import com.easygame.api.response.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@AllArgsConstructor
public class GameScoreTokenProvider {
    private final HmacProperties hmacProperties;

    public String generate(String gameType, String nickName, int score, String jti) {
        String data = String.join(":", gameType, nickName, String.valueOf(score), jti);
        return hmac(data);
    }

    public void isValidSignedToken(String gameType, String nickName, int score, String jti, String signedToken) throws IllegalAccessException {
        String data = String.join(":", gameType, nickName, String.valueOf(score), jti);
        String expected = hmac(data);

        if(!expected.equals(signedToken)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }

    }

    private String hmac(String data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(hmacProperties.getSecretKey().getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Fail to create token with secret key", e);
        }
    }
}

package com.easygame.api;

import com.easygame.api.exception.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtTokenUtil {
    private final JwtTokenProvider jwtTokenProvider;

    public String getNickNameByResolvedToken(String headerToken) {
        String token = resolveToken(headerToken);

        if (!jwtTokenProvider.isValidToken(token)) {
            throw new InvalidTokenException("invalid token");
        }

        return jwtTokenProvider.getNickname(token);

    }

    public String createTokenWithNickName(String nickName) {
        return jwtTokenProvider.createToken(nickName);
    }

    private String resolveToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new InvalidTokenException("cannot resolve token");
    }

}

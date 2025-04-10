package com.easygame.api;

import com.easygame.api.exception.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtTokenUtil {
    private final JwtTokenProvider jwtTokenProvider;
    private final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    public String getNickNameByResolvedToken(String headerToken) {
        String token = resolveToken(headerToken);

        if (!jwtTokenProvider.isValidToken(token)) {
            throw new InvalidTokenException("Invalid token");
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

    public Authentication getAuthentication(String nickName) {
        UserDetails userDetails = new CustomUserDetails(nickName);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}

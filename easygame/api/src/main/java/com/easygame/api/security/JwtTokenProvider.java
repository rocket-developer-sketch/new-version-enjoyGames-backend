package com.easygame.api.security;

import com.easygame.api.configuration.JwtProperties;
import com.easygame.api.exception.InvalidTokenException;
import com.easygame.api.response.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    public String getJti(String requestToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                .build()
                .parseClaimsJws(getResolvedToken(requestToken))
                .getBody();

        return claims.getId();
    }

    public String getNickName(String requestToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                .build()
                .parseClaimsJws(getResolvedToken(requestToken))
                .getBody();

        return claims.getSubject();
    }

    public String getGameType(String requestToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey().getBytes())
                .build()
                .parseClaimsJws(getResolvedToken(requestToken))
                .getBody();

        return claims.get("gameType", String.class);
    }

    public JwtToken createToken(String gameType, String nickName) {
        if(nickName == null || nickName.isBlank()) {
            throw new IllegalArgumentException("Empty nickname");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());
        String jti = UUID.randomUUID().toString();
        Claims claims = Jwts.claims().setSubject(nickName).add("gameType", gameType).build();

        String token = Jwts.builder()
//                .setSubject(nickName)
                .setClaims(claims)
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes()), SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder().gameType(gameType).jti(jti).token(token).nickName(nickName).build();
    }

    public Authentication getAuthentication(String gameType, String userName, String nickName) {
        UserDetails userDetails = new CustomUserDetails(gameType, userName, nickName);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getResolvedToken(String token) {
        String resolvedToken = resolveToken(token);

        if (!isValidToken(resolvedToken)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }
        return resolvedToken;
    }

    private String resolveToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new InvalidTokenException(ErrorCode.TOKEN_RESOLVE_FAILED);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecretKey().getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

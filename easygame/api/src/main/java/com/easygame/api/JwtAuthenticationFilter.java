package com.easygame.api;

import com.easygame.api.configuration.AuthProperties;
import com.easygame.api.response.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthProperties authProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, AuthProperties authProperties) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authProperties = authProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        boolean isExcluded = authProperties.getExcludePaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));

        if (isExcluded) {
            filterChain.doFilter(request, response); // no authentication needed
            return;
        }

        String header = request.getHeader("Authorization");

        try {
            String token = jwtTokenUtil.getNickNameByResolvedToken(header); // parsing
            Authentication auth = jwtTokenUtil.getAuthentication(token); // auth create
            SecurityContextHolder.getContext().setAuthentication(auth); // save auth
        } catch (Exception e) {
            log.info("API Authentication Exception : {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ErrorCode.INVALID_TOKEN.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}

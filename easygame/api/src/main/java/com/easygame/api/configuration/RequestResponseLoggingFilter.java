package com.easygame.api.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Order(1)
public class RequestResponseLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        chain.doFilter(wrappedRequest, wrappedResponse);

        // 로그 출력
        logRequest(wrappedRequest);
        logResponse(wrappedResponse);

        // response를 클라이언트로 전달하기 위해 반드시 복사
        wrappedResponse.copyBodyToResponse();
    }

    private void logRequest(ContentCachingRequestWrapper request) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("[REQUEST] {} {}", method, uri);

        // Body 출력
        if (!body.isBlank()) {
            log.debug("Request Body: {}", body);
        }

        // Query Parameter 출력
        Map<String, String[]> params = request.getParameterMap();
        if (!params.isEmpty()) {
            params.forEach((key, value) -> {
                log.debug("Request Param: ( {} ) = {}", key, String.join(",", value));
            });
        }
    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        int status = response.getStatus();

        log.info("[RESPONSE] HTTP {}", status);

        String contentType = response.getContentType();
        if(contentType != null && contentType.startsWith("application/json")) {
            String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            if(!body.isBlank()) {
                log.debug("Response Body: {}", body);
            }
        }
    }
}

package com.easygame.api.configuration;

import com.easygame.api.response.ErrorCode;
import com.easygame.api.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

//public class RequestResponseLoggingFilter implements Filter {
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse); // execute
        } catch (Exception e) {
            log.debug("Filter exception : {}", e.getMessage(), e);
        } finally {
            // logging
            logRequest(wrappedRequest);
            logResponse(wrappedResponse);

            // Must copy the response body to ensure it's sent to the client
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("[REQUEST] {} {}", method, uri);

        // Body
        if (!body.isBlank()) {
            log.debug("Request Body: {}", body);
        }

        // Query Parameter
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

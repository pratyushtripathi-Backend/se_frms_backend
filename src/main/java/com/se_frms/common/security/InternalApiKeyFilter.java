package com.se_frms.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Protects backend-to-backend endpoints that must not be exposed to browser clients.
 */
@Slf4j
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_KEY_HEADER = "X-INTERNAL-API-KEY";

    @Value("${app.internal.api-key:}")
    private String configuredApiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.startsWith("/api/v1/internal/")
                && !uri.startsWith("/api/v1/admin/internal/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String suppliedApiKey = request.getHeader(INTERNAL_API_KEY_HEADER);

        if (!hasValidApiKey(suppliedApiKey)) {
            log.warn("Internal API-key authentication failed, uri={}", request.getRequestURI());
            writeUnauthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasValidApiKey(String suppliedApiKey) {
        if (configuredApiKey == null || configuredApiKey.isBlank()
                || suppliedApiKey == null || suppliedApiKey.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                configuredApiKey.getBytes(StandardCharsets.UTF_8),
                suppliedApiKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                """
                {
                  "status": false,
                  "responseCode": 401,
                  "responseMessage": "Valid internal API key is required.",
                  "responseData": null
                }
                """
        );
    }
}

package com.se_frms.common.security;

import com.se_frms.auth.repository.BlacklistedTokenRepository;
import com.se_frms.auth.repository.SessionStoreRepository;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final SessionStoreRepository sessionStoreRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            if (uri.startsWith("/api/v1/auth")) {

                log.debug("Authentication skipped for public auth endpoint, uri={}", uri);

                filterChain.doFilter(request, response);

                return;
            }

            log.warn("Authentication failed because token was missing, uri={}", uri);

            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication required. Please login."
            );

            return;
        }

        String token = authHeader.substring(7);

        if (blacklistedTokenRepository.existsByToken(token)) {

            log.warn("Authentication failed because token is blacklisted, uri={}", uri);

            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expired. Please login again."
            );

            return;
        }

        boolean valid = jwtUtil.validateToken(token);

        if (!sessionStoreRepository.existsByTokenAndStatus(token, true)) {

            log.warn("Authentication failed because session is inactive, uri={}", uri);

            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Session inactive. Please login again."
            );

            return;
        }

        if (!valid) {

            log.warn("Authentication failed because token is invalid or expired, uri={}", uri);

            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token. Please login again."
            );

            return;
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {

            log.warn("Authentication failed because user was not found, uri={}", uri);

            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "User not found. Please login again."
            );

            return;
        }

        if (Boolean.FALSE.equals(user.getStatus())) {

            log.warn("Authentication failed because user is blacklisted, userId={}, uri={}", user.getId(), uri);

            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "User is blocked. Please contact admin."
            );

            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Authentication successful, userId={}, uri={}", user.getId(), uri);

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                """
                {
                  "status": false,
                  "responseCode": %d,
                  "responseMessage": "%s",
                  "responseData": null
                }
                """.formatted(status, message)
        );
    }
}

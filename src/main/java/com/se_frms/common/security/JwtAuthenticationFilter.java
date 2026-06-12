package com.se_frms.common.security;

import com.se_frms.auth.repository.BlacklistedTokenRepository;
import com.se_frms.auth.repository.SessionStoreRepository;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.se_frms.auth.repository.SessionStoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import com.se_frms.user.model.User;
import java.time.LocalDateTime;
import com.se_frms.auth.repository.BlacklistedTokenRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

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

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            if (uri.startsWith("/api/v1/auth")) {

                log.debug(
                        "Authentication skipped for public auth endpoint, uri={}",
                        uri
                );

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            log.warn(
                    "Authentication failed because token was missing, uri={}",
                    uri
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                      "status": false,
                      "responseCode": 401,
                      "responseMessage": "Authentication required. Please login.",
                      "responseData": null
                    }
                    """
            );

            return;
        }

        String token =
                authHeader.substring(7);

        if (blacklistedTokenRepository.existsByToken(token)) {

            log.warn(
                    "Authentication failed because token is blacklisted, uri={}",
                    uri
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                      "status": false,
                      "responseCode": 401,
                      "responseMessage": "Token expired. Please login again."
                    }
                    """
            );

            return;
        }

        boolean valid =
                jwtUtil.validateToken(token);

        if (!sessionStoreRepository.existsByTokenAndStatus(token, true)) {

            log.warn(
                    "Authentication failed because session is inactive, uri={}",
                    uri
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                      "status": false,
                      "responseCode": 401,
                      "responseMessage": "Session inactive. Please login again."
                    }
                    """
            );

            return;
        }

        if (!valid) {

            log.warn(
                    "Authentication failed because token is invalid or expired, uri={}",
                    uri
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                      "status": false,
                      "responseCode": 401,
                      "responseMessage": "Invalid or expired token. Please login again.",
                      "responseData": null
                    }
                    """
            );

            return;
        }

        String email =
                jwtUtil.extractEmail(token);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Authentication failed because user was not found, uri={}",
                                            uri
                                    );

                                    return new RuntimeException(
                                            "User not found"
                                    );
                                }
                        );

//        if (
//                user.getLastActivity() != null
//                        &&
//                        user.getLastActivity()
//                                .plusMinutes(10)
//                                .isBefore(
//                                        LocalDateTime.now()
//                                )
//        ) {
//
//            response.setStatus(
//                    HttpServletResponse.SC_UNAUTHORIZED
//            );
//
//            response.setContentType(
//                    "application/json"
//            );
//
//            response.getWriter().write(
//                    """
//                    {
//                      "status": false,
//                      "responseCode": 401,
//                      "responseMessage": "Session expired due to inactivity"
//                    }
//                    """
//            );
//
//            return;
//        }
        UserDetails userDetails =
                userDetailsService
                        .loadUserByUsername(email);


        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );
//        user.setLastActivity(
//                LocalDateTime.now()
//        );

        userRepository.save(user);

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        log.debug(
                "Authentication successful, userId={}, uri={}",
                user.getId(),
                uri
        );

        filterChain.doFilter(
                request,
                response
        );
    }
}
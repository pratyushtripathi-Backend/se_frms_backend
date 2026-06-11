package com.se_frms.common.security;

import com.se_frms.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.se_frms.auth.repository.SessionStoreRepository;

import lombok.RequiredArgsConstructor;

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

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            String uri =
                    request.getRequestURI();

            if (
                    uri.startsWith(
                            "/api/v1/auth"
                    )
            ) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

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
        if (
                blacklistedTokenRepository
                        .existsByToken(token)
        ) {

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
                jwtUtil.validateToken(
                        token
                );
                jwtUtil.validateToken(token);
        if (!sessionStoreRepository.existsByTokenAndStatus(token, true)) {

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
                                () -> new RuntimeException(
                                        "User not found"
                                )
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

        filterChain.doFilter(
                request,
                response
        );
    }
}
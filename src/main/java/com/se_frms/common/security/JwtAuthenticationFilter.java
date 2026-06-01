package com.se_frms.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;

//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authHeader =
//                request.getHeader("Authorization");
//
//        if (authHeader == null
//                || !authHeader.startsWith("Bearer ")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token =
//                authHeader.substring(7);
//
//        if (!jwtUtil.validateToken(token)) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String email =
//                jwtUtil.extractEmail(token);
//
//        UserDetails userDetails =
//                userDetailsService
//                        .loadUserByUsername(email);
//
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );
//
//        authentication.setDetails(
//                new WebAuthenticationDetailsSource()
//                        .buildDetails(request)
//        );
//
//        SecurityContextHolder
//                .getContext()
//                .setAuthentication(authentication);
//
//        filterChain.doFilter(
//                request,
//                response
//        );
//    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("=== FILTER START ===");

        String authHeader =
                request.getHeader("Authorization");

        System.out.println("AUTH HEADER = " + authHeader);

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            System.out.println("NO TOKEN FOUND");

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(7);

        System.out.println("TOKEN = " + token);

        boolean valid =
                jwtUtil.validateToken(token);

        System.out.println("TOKEN VALID = " + valid);

        if (!valid) {

            System.out.println("INVALID TOKEN");

            filterChain.doFilter(request, response);
            return;
        }

        String email =
                jwtUtil.extractEmail(token);

        System.out.println("EMAIL = " + email);

        UserDetails userDetails =
                userDetailsService
                        .loadUserByUsername(email);

        System.out.println(
                "USER FOUND = "
                        + userDetails.getUsername()
        );

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

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        System.out.println("=== FILTER END ===");

        filterChain.doFilter(
                request,
                response
        );
    }
}
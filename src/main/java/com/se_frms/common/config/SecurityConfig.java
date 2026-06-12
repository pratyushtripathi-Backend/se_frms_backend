package com.se_frms.common.config;

import com.se_frms.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.MediaType;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            401
                                    );
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.setCharacterEncoding(
                                            "UTF-8"
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
                                }
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            403
                                    );
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.setCharacterEncoding(
                                            "UTF-8"
                                    );
                                    response.getWriter().write(
                                            """
                                            {
                                              "status": false,
                                              "responseCode": 403,
                                              "responseMessage": "Access denied. Admin role required.",
                                              "responseData": null
                                            }
                                            """
                                    );
                                }
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/error"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/v1/auth/login-history",
                                "/api/v1/auth/login-history/**",
                                "/api/v1/auth/login-attempt",
                                "/api/v1/auth/login-attempt/**"
                        )
                        .hasRole(
                                "ADMIN"
                        )

                        .requestMatchers(
                                "/api/v1/auth/change-password"
                        )
                        .authenticated()

                        .requestMatchers(

                                "/api/v1/auth/**"

                        )
                        .permitAll()

                        .requestMatchers(

                                "/api/v1/access/**"

                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/api/v1/permissions/**"
                        )
                        .hasRole(
                                "ADMIN"
                        )

                        .requestMatchers(
                                "/api/v1/employee/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "EMPLOYEE"
                        )


                        .anyRequest()
                        .authenticated()



                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}

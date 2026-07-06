package com.se_frms.common.config;

import com.se_frms.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .cors(Customizer.withDefaults())

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
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/v1/auth/login-history",
                                "/api/v1/auth/login-history/**",
                                "/api/v1/auth/login-attempt",
                                "/api/v1/auth/login-attempt/**",
                                "/api/v1/auth/sessions",
                                "/api/v1/auth/sessions/**"
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
                        .hasRole("ADMIN")

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://localhost:3000",
                        "http://127.0.0.1:3000"
                )
        );
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );
        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "X-Requested-With"
                )
        );
        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );
        configuration.setAllowCredentials(
                true
        );
        configuration.setMaxAge(
                3600L
        );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}

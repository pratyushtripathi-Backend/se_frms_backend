package com.se_frms.auth.controller;


import com.se_frms.auth.dto.*;

import com.se_frms.auth.repository.LoginAttemptRepository;
import com.se_frms.auth.service.AuthService;

import com.se_frms.auth.service.LoginAttemptService;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.se_frms.auth.dto.SendOtpRequestDTO;
import com.se_frms.auth.dto.VerifyOtpRequestDTO;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final LoginAttemptService loginAttemptService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO<RegistrationResponseDTO>>
    registerUser(
            @Valid
            @RequestBody
            UserRegistrationRequest request
    ) {

        RegistrationResponseDTO responseData =
                authService.registerUser(request);

        AuthResponseDTO<RegistrationResponseDTO> response =
                AuthResponseDTO
                        .<RegistrationResponseDTO>builder()
                        .status(true)
                        .responseCode(201)
                        .responseMessage("User registered successfully")
                        .responseData(responseData)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO<LoginResponseDTO>>
    login(

            @Valid
            @RequestBody
            LoginRequestDTO request,

            HttpServletRequest httpRequest

    ) {

        LoginResponseDTO responseData =

                authService.login(

                        request,

                        httpRequest

                );

        AuthResponseDTO<LoginResponseDTO> response =

                AuthResponseDTO
                        .<LoginResponseDTO>builder()

                        .status(true)

                        .responseCode(200)

                        .responseMessage(
                                "Login successful"
                        )

                        .responseData(
                                responseData
                        )

                        .build();

        return ResponseEntity.ok(
                response
        );
    }

    @GetMapping(
            "/login-history"
    )
    public ResponseEntity<
            AuthResponseDTO<
                    List<LoginHistoryResponseDTO>
                    >
            >

    getLoginHistory() {

        return ResponseEntity.ok(

                AuthResponseDTO

                        .<List<LoginHistoryResponseDTO>>
                                builder()

                        .status(
                                true
                        )

                        .responseCode(
                                200
                        )

                        .responseMessage(
                                "Login history fetched successfully"
                        )

                        .responseData(

                                authService
                                        .getLoginHistory()

                        )

                        .build()
        );
    }

    @GetMapping(
            "/login-history/{userId}"
    )
    public ResponseEntity<
            AuthResponseDTO<
                    List<LoginHistoryResponseDTO>
                    >
            >

    getLoginHistoryByUserId(

            @PathVariable
            UUID userId

    ) {

        return ResponseEntity.ok(

                AuthResponseDTO

                        .<List<LoginHistoryResponseDTO>>
                                builder()

                        .status(
                                true
                        )

                        .responseCode(
                                200
                        )

                        .responseMessage(
                                "Login history fetched successfully"
                        )

                        .responseData(

                                authService
                                        .getLoginHistoryByUserId(
                                                userId
                                        )

                        )

                        .build()
        );
    }

    @GetMapping("/login-attempt")
    public ResponseEntity<AuthResponseDTO<List<LoginAttemptResponseDTO>>> getAllLoginAttempts() {

        List<LoginAttemptResponseDTO> response =
                loginAttemptService.getAllLoginAttempts();

        return ResponseEntity.ok(

                AuthResponseDTO.<List<LoginAttemptResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login attempts fetched successfully")
                        .responseData(response)
                        .build()

        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponseDTO<Object>>
    resetPassword(
            @Valid
            @RequestBody
            ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Password reset successful"
                        )
                        .responseData(null)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponseDTO<Object>>
    forgotPassword(
            @Valid
            @RequestBody
            ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Password reset link sent"
                        )
                        .responseData(null)
                        .build()
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @Valid
            @RequestBody
            SendOtpRequestDTO request
    ) {

        authService.sendOtp(request);

        return ResponseEntity.ok(
                "OTP sent successfully"
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDTO<LoginResponseDTO>>
    verifyOtp(
            @Valid
            @RequestBody
            VerifyOtpRequestDTO request
    ) {

        LoginResponseDTO responseData =
                authService.verifyOtp(request);

        AuthResponseDTO<LoginResponseDTO> response =
                AuthResponseDTO
                        .<LoginResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "OTP Login successful"
                        )
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/test")
    public String test(Authentication authentication) {

        System.out.println(authentication);

        return "Authenticated User";
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO<Object>>
    logout(
            HttpServletRequest request
    ) {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            throw new RuntimeException(
                    "Token not found"
            );
        }

        String token =
                authHeader.substring(7);

        authService.logout(token);

        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Logout successful"
                        )
                        .responseData(null)
                        .build()
        );
    }

    @GetMapping(
            "/login-attempt/{userId}"
    )
    public ResponseEntity<?> getLoginAttemptsByUserId(

            @PathVariable
            UUID userId

    ) {

        List<LoginAttemptResponseDTO> response =

                loginAttemptService
                        .getLoginAttemptsByUserId(
                                userId
                        );

        return ResponseEntity.ok(

                Map.of(

                        "success", true,

                        "message",

                        response.isEmpty()

                                ?

                                "No login attempts found"

                                :

                                "Login attempts fetched successfully",

                        "count",

                        response.size(),

                        "data",

                        response
                )
        );
    }
}


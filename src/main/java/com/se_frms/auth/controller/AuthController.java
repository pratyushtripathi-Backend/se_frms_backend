package com.se_frms.auth.controller;


import com.se_frms.auth.dto.*;

import com.se_frms.auth.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.se_frms.auth.dto.SendOtpRequestDTO;
import com.se_frms.auth.dto.VerifyOtpRequestDTO;
import org.springframework.security.core.Authentication;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
            LoginRequestDTO request
    ) {

        LoginResponseDTO responseData =
                authService.login(request);

        AuthResponseDTO<LoginResponseDTO> response =
                AuthResponseDTO
                        .<LoginResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login successful")
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
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
}


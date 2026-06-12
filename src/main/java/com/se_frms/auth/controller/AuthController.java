package com.se_frms.auth.controller;

import com.se_frms.auth.dto.*;
import com.se_frms.auth.service.AuthService;
import com.se_frms.auth.service.LoginAttemptService;
import com.se_frms.auth.service.SessionStoreService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SessionStoreService sessionStoreService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO<RegistrationResponseDTO>>
    registerUser(
            @Valid
            @RequestBody
            UserRegistrationRequest request
    ) {

        log.info("Register user request received");

        RegistrationResponseDTO responseData =
                authService.registerUser(request);

        log.info("User registered successfully");

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

        log.info(
                "Login request received, ip={}",
                httpRequest.getRemoteAddr()
        );

        LoginResponseDTO responseData =
                authService.login(
                        request,
                        httpRequest
                );

        log.info(
                "Login successful, ip={}",
                httpRequest.getRemoteAddr()
        );

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

    @GetMapping("/login-history")
    public ResponseEntity<AuthResponseDTO<List<LoginHistoryResponseDTO>>>
    getLoginHistory() {

        log.info("Fetch login history request received");

        List<LoginHistoryResponseDTO> responseData =
                authService.getLoginHistory();

        log.info(
                "Login history fetched successfully, count={}",
                responseData.size()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<LoginHistoryResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login history fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/login-history/{userId}")
    public ResponseEntity<AuthResponseDTO<List<LoginHistoryResponseDTO>>>
    getLoginHistoryByUserId(
            @PathVariable
            Integer userId

    ) {

        log.info(
                "Fetch login history request received, userId={}",
                userId
        );

        List<LoginHistoryResponseDTO> responseData =
                authService.getLoginHistoryByUserId(userId);

        log.info(
                "Login history fetched successfully, userId={}, count={}",
                userId,
                responseData.size()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<LoginHistoryResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login history fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/login-attempt")
    public ResponseEntity<AuthResponseDTO<List<LoginAttemptResponseDTO>>>
    getAllLoginAttempts() {

        log.info("Fetch all login attempts request received");

        List<LoginAttemptResponseDTO> response =
                loginAttemptService.getAllLoginAttempts();

        log.info(
                "Login attempts fetched successfully, count={}",
                response.size()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<LoginAttemptResponseDTO>>builder()
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

        log.info("Reset password request received");

        authService.resetPassword(request);

        log.info("Password reset successful");

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<Object>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Password reset successful")
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

        log.info("Forgot password request received");

        authService.forgotPassword(request);

        log.info("Password reset link sent successfully");

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<Object>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Password reset link sent")
                        .responseData(null)
                        .build()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthResponseDTO<Object>>
    changePassword(
            @Valid
            @RequestBody
            ChangePasswordRequest request
    ) {

        authService.changePassword(request);

        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Password changed successfully"
                        )
                        .responseData(null)
                        .build()
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String>
    sendOtp(
            @Valid
            @RequestBody
            SendOtpRequestDTO request
    ) {

        log.info("Send OTP request received");

        authService.sendOtp(request);

        log.info("OTP sent successfully");

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDTO<LoginResponseDTO>>
    verifyOtp(
            @Valid
            @RequestBody
            VerifyOtpRequestDTO request
    ) {

        log.info("Verify OTP request received");

        LoginResponseDTO responseData =
                authService.verifyOtp(request);

        log.info("OTP verified successfully");

        AuthResponseDTO<LoginResponseDTO> response =
                AuthResponseDTO
                        .<LoginResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("OTP Login successful")
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test(
            Authentication authentication
    ) {

        log.debug(
                "Authenticated user test endpoint called, principal={}",
                authentication != null
                        ? authentication.getName()
                        : "anonymous"
        );

        return "Authenticated User";
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO<Object>>
    logout(
            HttpServletRequest request
    ) {

        log.info("Logout request received");

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            log.warn("Logout failed because token was not found");

            throw new RuntimeException("Token not found");
        }

        String token =
                authHeader.substring(7);

        authService.logout(token);

        log.info("Logout successful");

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<Object>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Logout successful")
                        .responseData(null)
                        .build()
        );
    }

    @GetMapping("/session/status")
    public ResponseEntity<AuthResponseDTO<SessionStatusResponseDTO>>
    getSessionStatus(
            HttpServletRequest request
    ) {

        log.info("Session status request received");

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            log.warn("Session status failed because token was not found");

            throw new RuntimeException("Token not found");
        }

        String token =
                authHeader.substring(7);

        SessionStatusResponseDTO responseData =
                sessionStoreService.getSessionStatus(token);

        log.info("Session status fetched successfully");

        AuthResponseDTO<SessionStatusResponseDTO> response =
                AuthResponseDTO
                        .<SessionStatusResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Session status fetched successfully")
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/login-attempt/{userId}")
    public ResponseEntity<?>
    getLoginAttemptsByUserId(
            @PathVariable
            Integer userId

    ) {

        log.info(
                "Fetch login attempts request received, userId={}",
                userId
        );

        List<LoginAttemptResponseDTO> response =
                loginAttemptService.getLoginAttemptsByUserId(userId);

        log.info(
                "Login attempts fetched successfully, userId={}, count={}",
                userId,
                response.size()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        response.isEmpty()
                                ? "No login attempts found"
                                : "Login attempts fetched successfully",
                        "count", response.size(),
                        "data", response
                )
        );
    }
}
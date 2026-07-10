package com.se_frms.auth.controller;

import com.se_frms.auth.dto.*;
import com.se_frms.auth.service.AuthService;
import com.se_frms.auth.service.LoginAttemptService;
import com.se_frms.auth.service.SessionStoreService;
import com.se_frms.common.dto.PagedResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.se_frms.common.dto.PagedResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<AuthResponseDTO<LoginOtpResponseDTO>>
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

        LoginOtpResponseDTO responseData =
                authService.login(
                        request,
                        httpRequest
                );

        log.info(
                "Password verified and OTP generated, ip={}",
                httpRequest.getRemoteAddr()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<LoginOtpResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("OTP sent to registered mobile number")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/login-history")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<LoginHistoryResponseDTO>>>
    getLoginHistory(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        log.info("Fetch login history request received");

        Page<LoginHistoryResponseDTO> pageData =
                authService.getLoginHistory(page, size, filters);

        PagedResponseDTO<LoginHistoryResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        log.info(
                "Login history fetched successfully, count={}",
                pageData.getNumberOfElements()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<LoginHistoryResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login history fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/login-history/{userId}")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<LoginHistoryResponseDTO>>>
    getLoginHistoryByUserId(
            @PathVariable
            Integer userId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        log.info("Fetch login history request received, userId={}", userId);

        Page<LoginHistoryResponseDTO> pageData =
                authService.getLoginHistoryByUserId(
                        userId,
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<LoginHistoryResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        log.info(
                "Login history fetched successfully, userId={}, count={}",
                userId,
                pageData.getNumberOfElements()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<LoginHistoryResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login history fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/login-attempt")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<LoginAttemptResponseDTO>>>
    getAllLoginAttempts(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        log.info("Fetch all login attempts request received");

        Page<LoginAttemptResponseDTO> pageData =
                loginAttemptService.getAllLoginAttempts(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<LoginAttemptResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        log.info(
                "Login attempts fetched successfully, count={}",
                pageData.getNumberOfElements()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<LoginAttemptResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login attempts fetched successfully")
                        .responseData(responseData)
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


    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDTO<LoginResponseDTO>>
    verifyOtp(
            @Valid
            @RequestBody
            VerifyOtpRequestDTO request,

            HttpServletRequest httpRequest
    ) {

        log.info("Verify OTP request received");

        LoginResponseDTO responseData =
                authService.verifyOtp(
                        request,
                        httpRequest
                );

        log.info("OTP verified and login successful");

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<LoginResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Login successful")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/test")
    public ResponseEntity<AuthResponseDTO<String>> test(
            Authentication authentication
    ) {

        log.debug(
                "Authenticated user test endpoint called, principal={}",
                authentication != null
                        ? authentication.getName()
                        : "anonymous"
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<String>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Authenticated User")
                        .responseData("Authenticated User")
                        .build()
        );
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
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<LoginAttemptResponseDTO>>>
    getLoginAttemptsByUserId(
            @PathVariable
            Integer userId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        log.info("Fetch login attempts request received, userId={}", userId);

        Page<LoginAttemptResponseDTO> pageData =
                loginAttemptService.getLoginAttemptsByUserId(
                        userId,
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<LoginAttemptResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        log.info(
                "Login attempts fetched successfully, userId={}, count={}",
                userId,
                pageData.getNumberOfElements()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<LoginAttemptResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                pageData.isEmpty()
                                        ? "No login attempts found"
                                        : "Login attempts fetched successfully"
                        )
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/sessions")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<SessionStatusResponseDTO>>>
    getAllSessions(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        Page<SessionStatusResponseDTO> pageData =
                sessionStoreService.getAllSessions(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<SessionStatusResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<SessionStatusResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Sessions fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

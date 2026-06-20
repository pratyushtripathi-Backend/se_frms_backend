package com.se_frms.auth.service;

import com.se_frms.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.lang.Integer;

public interface AuthService {

    RegistrationResponseDTO registerUser(
            UserRegistrationRequest request
    );

    void forgotPassword(
            ForgotPasswordRequest request
    );

    void resetPassword(
            ResetPasswordRequest request
    );

    void changePassword(
            ChangePasswordRequest request
    );

    LoginOtpResponseDTO login(
            LoginRequestDTO request,
            HttpServletRequest httpRequest
    );

    LoginResponseDTO verifyOtp(
            VerifyOtpRequestDTO request,
            HttpServletRequest httpRequest
    );
    void logout(String token);

    Page<LoginHistoryResponseDTO>
    getLoginHistory(
            int page,
            int size,
            Map<String, String> filters
    );



    Page<LoginHistoryResponseDTO>
    getLoginHistoryByUserId(
            Integer userId,
            int page,
            int size,
            Map<String, String> filters
    );
}
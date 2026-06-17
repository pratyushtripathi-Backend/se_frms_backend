package com.se_frms.auth.service;

import com.se_frms.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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

    void logout(
            String token
    );

    List<LoginHistoryResponseDTO> getLoginHistory();

    List<LoginHistoryResponseDTO> getLoginHistoryByUserId(
            Integer userId
    );
}
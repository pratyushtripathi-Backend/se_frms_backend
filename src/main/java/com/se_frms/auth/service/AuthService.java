package com.se_frms.auth.service;

import com.se_frms.admin.dto.CreateEmployeeRequest;
import com.se_frms.auth.dto.LoginRequestDTO;
import com.se_frms.auth.dto.LoginResponseDTO;
import com.se_frms.auth.dto.ForgotPasswordRequest;
import com.se_frms.auth.dto.ResetPasswordRequest;
import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface AuthService {

    RegistrationResponseDTO registerUser(
            UserRegistrationRequest request );

    void forgotPassword(
            ForgotPasswordRequest request
    );

    void resetPassword(
            ResetPasswordRequest request
    );


    LoginResponseDTO login(

            LoginRequestDTO request,

            HttpServletRequest httpRequest

    );




    void sendOtp(
            SendOtpRequestDTO request
    );

    LoginResponseDTO verifyOtp(
            VerifyOtpRequestDTO request
    );
    void logout(String token);

    List<LoginHistoryResponseDTO>
    getLoginHistory();


    List<LoginHistoryResponseDTO>
    getLoginHistoryByUserId(

            UUID userId

    );


}
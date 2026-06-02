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
            LoginRequestDTO request
    );

    void sendOtp(
            SendOtpRequestDTO request
    );

    LoginResponseDTO verifyOtp(
            VerifyOtpRequestDTO request
    );

    RegistrationResponseDTO createEmployee(
            CreateEmployeeRequest request
    );
}
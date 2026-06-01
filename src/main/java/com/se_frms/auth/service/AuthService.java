package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginRequestDTO;
import com.se_frms.auth.dto.LoginResponseDTO;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.dto.UserRegistrationRequest;

public interface AuthService {

    RegistrationResponseDTO registerUser(
            UserRegistrationRequest request
    );

    LoginResponseDTO login(
            LoginRequestDTO request
    );
}
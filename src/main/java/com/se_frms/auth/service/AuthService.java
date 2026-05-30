package com.se_frms.auth.service;

import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.auth.dto.RegistrationResponseDTO;
public interface AuthService {
    RegistrationResponseDTO registerUser(
            UserRegistrationRequest request );
}

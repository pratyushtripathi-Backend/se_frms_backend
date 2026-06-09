package com.se_frms.auth.service;



import com.se_frms.auth.dto.LoginAttemptResponseDTO;
import com.se_frms.user.model.User;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LoginAttemptService {

    void saveAttempt(

            User user,

            String email,

            Boolean status,

            String reason,

            BigDecimal latitude,

            BigDecimal longitude,

            HttpServletRequest request
    );

    List<LoginAttemptResponseDTO>
    getAllLoginAttempts();


    List<LoginAttemptResponseDTO>
    getLoginAttemptsByUserId(UUID userId);
}
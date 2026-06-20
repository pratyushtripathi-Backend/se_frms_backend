package com.se_frms.auth.service;



import com.se_frms.auth.dto.LoginAttemptResponseDTO;
import com.se_frms.user.model.User;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.lang.Integer;

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

    Page<LoginAttemptResponseDTO>
    getAllLoginAttempts(
            int page,
            int size,
            Map<String, String> filters
    );


    Page<LoginAttemptResponseDTO>
    getLoginAttemptsByUserId(
            Integer userId,
            int page,
            int size,
            Map<String, String> filters
    );
}

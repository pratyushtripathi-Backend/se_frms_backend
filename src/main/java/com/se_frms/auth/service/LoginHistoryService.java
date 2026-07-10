package com.se_frms.auth.service;



import com.se_frms.auth.dto.LoginHistoryResponseDTO;
import com.se_frms.auth.model.LoginHistory;
import com.se_frms.user.model.User;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.lang.Integer;

public interface LoginHistoryService {

    void saveLoginHistory(

            User user,

            HttpServletRequest request,

            Boolean status,

            String macAddress,

            BigDecimal latitude,

            BigDecimal longitude
    );

    Page<LoginHistoryResponseDTO>
    getLoginHistory(
            User user,
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

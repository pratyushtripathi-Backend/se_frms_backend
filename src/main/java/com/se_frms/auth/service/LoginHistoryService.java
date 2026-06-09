package com.se_frms.auth.service;



import com.se_frms.auth.dto.LoginHistoryResponseDTO;
import com.se_frms.auth.model.LoginHistory;
import com.se_frms.user.model.User;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface LoginHistoryService {

    void saveLoginHistory(

            User user,

            HttpServletRequest request,

            Boolean status
    );

    List<LoginHistoryResponseDTO>
    getLoginHistory(
            User user
    );

    List<LoginHistoryResponseDTO>
    getLoginHistoryByUserId(
            UUID userId
    );
}

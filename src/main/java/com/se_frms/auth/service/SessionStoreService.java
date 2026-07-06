package com.se_frms.auth.service;

import com.se_frms.auth.dto.SessionStatusResponseDTO;
import com.se_frms.user.model.User;
import org.springframework.data.domain.Page;

public interface SessionStoreService {

    void createSession(User user, String token);

    void deactivateSession(String token);

    SessionStatusResponseDTO getSessionStatus(String token);
    Page<SessionStatusResponseDTO> getAllSessions(int page, int size);
}
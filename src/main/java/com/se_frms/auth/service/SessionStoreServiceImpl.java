package com.se_frms.auth.service;

import com.se_frms.auth.dto.SessionStatusResponseDTO;
import com.se_frms.auth.model.SessionStore;
import com.se_frms.auth.repository.SessionStoreRepository;
import com.se_frms.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionStoreServiceImpl
        implements SessionStoreService {

    private final SessionStoreRepository sessionStoreRepository;

    @Override
    @Transactional
    public void createSession(User user, String token) {

        SessionStore sessionStore =
                SessionStore.builder()
                        .user(user)
                        .token(token)
                        .status(true)
                        .createdBy(user)
                        .build();

        sessionStoreRepository.save(sessionStore);
    }

    @Override
    @Transactional
    public void deactivateSession(String token) {

        sessionStoreRepository
                .findByTokenAndStatus(token, true)
                .ifPresent(sessionStore -> {
                    sessionStore.setStatus(false);
                    sessionStoreRepository.save(sessionStore);
                });
    }
    @Override
    @Transactional(readOnly = true)
    public SessionStatusResponseDTO getSessionStatus(String token) {

        SessionStore sessionStore =
                sessionStoreRepository
                        .findByToken(token)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Session not found"
                                )
                        );

        User user = sessionStore.getUser();

        return SessionStatusResponseDTO
                .builder()
                .active(sessionStore.getStatus())
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getUserType())
                .sessionActiveDate(
                        sessionStore.getSessionActiveDate()
                )
                .sessionActiveTime(
                        sessionStore.getSessionActiveTime()
                )
                .build();
    }
}
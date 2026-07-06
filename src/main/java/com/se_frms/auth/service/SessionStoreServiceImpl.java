package com.se_frms.auth.service;

import com.se_frms.auth.dto.SessionStatusResponseDTO;
import com.se_frms.auth.model.SessionStore;
import com.se_frms.auth.repository.SessionStoreRepository;
import com.se_frms.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionStoreServiceImpl
        implements SessionStoreService {

    private final SessionStoreRepository sessionStoreRepository;

    @Override
    @Transactional
    public void createSession(
            User user,
            String token
    ) {

        log.info(
                "Create session service started, userId={}",
                user.getId()
        );

        SessionStore sessionStore =
                SessionStore.builder()
                        .user(user)
                        .token(token)
                        .status(true)
                        .createdBy(user)
                        .build();

        sessionStoreRepository.save(sessionStore);

        log.info(
                "Session created successfully, userId={}",
                user.getId()
        );
    }

    @Override
    @Transactional
    public void deactivateSession(
            String token
    ) {

        log.info("Deactivate session service started");

        sessionStoreRepository
                .findByTokenAndStatus(token, true)
                .ifPresentOrElse(
                        sessionStore -> {
                            sessionStore.setStatus(false);
                            sessionStoreRepository.save(sessionStore);

                            log.info(
                                    "Session deactivated successfully, userId={}",
                                    sessionStore.getUser().getId()
                            );
                        },
                        () -> log.warn(
                                "Deactivate session skipped because active session was not found"
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public SessionStatusResponseDTO getSessionStatus(
            String token
    ) {

        log.info("Get session status service started");

        SessionStore sessionStore =
                sessionStoreRepository
                        .findByToken(token)
                        .orElseThrow(
                                () -> {
                                    log.warn("Session status fetch failed because session was not found");

                                    return new RuntimeException(
                                            "Session not found"
                                    );
                                }
                        );

        User user = sessionStore.getUser();

        log.info(
                "Session status fetched successfully, userId={}, active={}",
                user.getId(),
                sessionStore.getStatus()
        );

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
    @Override
    @Transactional(readOnly = true)
    public Page<SessionStatusResponseDTO> getAllSessions(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createdDate")
                );

        return sessionStoreRepository
                .findAll(pageable)
                .map(this::toSessionStatusResponseDTO);
    }

    private SessionStatusResponseDTO toSessionStatusResponseDTO(
            SessionStore sessionStore
    ) {

        User user = sessionStore.getUser();

        return SessionStatusResponseDTO
                .builder()
                .active(sessionStore.getStatus())
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getUserType())
                .sessionActiveDate(sessionStore.getSessionActiveDate())
                .sessionActiveTime(sessionStore.getSessionActiveTime())
                .build();
    }
}
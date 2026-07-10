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
import com.se_frms.common.util.DynamicFilterSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionStoreServiceImpl
        implements SessionStoreService {

    private final SessionStoreRepository sessionStoreRepository;
    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("userId", "user.id"),
                    Map.entry("email", "user.email"),
                    Map.entry("firstName", "user.firstName"),
                    Map.entry("lastName", "user.lastName"),
                    Map.entry("role", "user.userType"),
                    Map.entry("active", "status"),
                    Map.entry("status", "status"),
                    Map.entry("sessionActiveDate", "sessionActiveDate"),
                    Map.entry("sessionActiveTime", "sessionActiveTime"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

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
            int size,
            Map<String, String> filters
    ) {

        Map<String, String> workingFilters =
                new HashMap<>(
                        filters == null
                                ? Map.of()
                                : filters
                );

        String search =
                workingFilters.remove("search");

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        workingFilters,
                        FILTER_FIELDS,
                        "createdDate",
                        Sort.Direction.DESC
                );

        Specification<SessionStore> specification =
                DynamicFilterSpecification.build(
                        workingFilters,
                        FILTER_FIELDS
                );

        Specification<SessionStore> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

        return sessionStoreRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::toSessionStatusResponseDTO);
    }

    private Specification<SessionStore> buildSearchSpecification(
            String search
    ) {

        if (search == null || search.isBlank()) {
            return null;
        }

        String keyword =
                "%"
                        + search.trim().toLowerCase(Locale.ROOT)
                        + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("user").get("email")
                                ),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("user").get("firstName")
                                ),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("user").get("lastName")
                                ),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("user").get("userType")
                                ),
                                keyword
                        )
                );
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
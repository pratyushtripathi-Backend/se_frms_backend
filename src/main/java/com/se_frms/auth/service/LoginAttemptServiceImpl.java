package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginAttemptResponseDTO;
import com.se_frms.auth.model.LoginAttempt;
import com.se_frms.auth.repository.LoginAttemptRepository;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.lang.Integer;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl
        implements LoginAttemptService {

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("userId", "user.id"),
                    Map.entry("email", "email"),
                    Map.entry("attemptStatus", "attemptStatus"),
                    Map.entry("status", "attemptStatus"),
                    Map.entry("failureReason", "failureReason"),
                    Map.entry("attemptReason", "failureReason"),
                    Map.entry("ipAddress", "ipAddress"),
                    Map.entry("latitude", "latitude"),
                    Map.entry("longitude", "longitude"),
                    Map.entry("url", "url"),
                    Map.entry("attemptedAt", "attemptedAt"),
                    Map.entry("createdBy", "createdBy.id")
            );

    private final LoginAttemptRepository repository;

    private final UserRepository userRepository;

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    @Override
    public void saveAttempt(
            User user,
            String email,
            Boolean status,
            String reason,
            BigDecimal latitude,
            BigDecimal longitude,
            HttpServletRequest request
    ) {

        log.info(
                "Saving login attempt, userId={}, status={}, reason={}",
                user != null ? user.getId() : null,
                status,
                reason
        );

        String ip =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (ip == null || ip.isBlank()) {
            ip =
                    request
                            .getRemoteAddr();
        }

        LoginAttempt entity =
                LoginAttempt
                        .builder()
                        .user(user)
                        .email(email)
                        .attemptStatus(status)
                        .failureReason(reason)
                        .ipAddress(ip)
                        .latitude(latitude)
                        .longitude(longitude)
                        .url(
                                request
                                        .getRequestURL()
                                        .toString()
                        )
                        .build();

        repository.save(
                entity
        );

        log.info(
                "Login attempt saved successfully, userId={}, status={}",
                user != null ? user.getId() : null,
                status
        );
    }

    @Override
    public Page<LoginAttemptResponseDTO> getAllLoginAttempts(
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info("Fetch all login attempts service started");

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        filters,
                        FILTER_FIELDS,
                        "attemptedAt",
                        Sort.Direction.DESC
                );

        Specification<LoginAttempt> specification =
                DynamicFilterSpecification.build(
                        filters,
                        FILTER_FIELDS
                );

        Page<LoginAttemptResponseDTO> response =
                repository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToDTO);

        log.info(
                "Login attempts fetched successfully, count={}",
                response.getNumberOfElements()
        );

        return response;
    }

    private LoginAttemptResponseDTO mapToDTO(
            LoginAttempt entity
    ) {

        return LoginAttemptResponseDTO
                .builder()
                .id(
                        entity.getId()
                )
                .userId(
                        entity.getUser() != null
                                ? entity.getUser().getId()
                                : null
                )
                .email(
                        entity.getEmail()
                )
                .attemptStatus(
                        entity.getAttemptStatus()
                )
                .attemptReason(
                        entity.getFailureReason()
                )
                .ipAddress(
                        entity.getIpAddress()
                )
                .latitude(
                        entity.getLatitude()
                )
                .longitude(
                        entity.getLongitude()
                )
                .url(
                        entity.getUrl()
                )
                .attemptedAt(
                        entity.getAttemptedAt()
                )
                .build();
    }

    @Override
    public Page<LoginAttemptResponseDTO>
    getLoginAttemptsByUserId(

            Integer userId,
            int page,
            int size,
            Map<String, String> filters

    ) {

        log.info(
                "Fetch login attempts by userId service started, userId={}",
                userId
        );

        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> {
                            log.warn(
                                    "Fetch login attempts failed because user was not found, userId={}",
                                    userId
                            );

                            return new RuntimeException(
                                    "User not found"
                            );
                        }
                );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        filters,
                        FILTER_FIELDS,
                        "attemptedAt",
                        Sort.Direction.DESC
                );

        Specification<LoginAttempt> specification =
                DynamicFilterSpecification
                        .<LoginAttempt>equal(
                                "user.id",
                                user.getId()
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        filters,
                                        FILTER_FIELDS
                                )
                        );

        Page<LoginAttemptResponseDTO> response =
                repository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToDTO);

        log.info(
                "Login attempts by userId fetched successfully, userId={}, count={}",
                userId,
                response.getNumberOfElements()
        );

        return response;
    }
}

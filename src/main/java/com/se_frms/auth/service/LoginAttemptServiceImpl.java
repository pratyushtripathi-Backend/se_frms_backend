package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginAttemptResponseDTO;
import com.se_frms.auth.model.LoginAttempt;
import com.se_frms.auth.repository.LoginAttemptRepository;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
import java.util.HashMap;
import java.util.Locale;
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
                    Map.entry("status", "status"),
                    Map.entry("reason", "reason"),
                    Map.entry("ipAddress", "ipAddress"),
                    Map.entry("attemptDate", "attemptDate"),
                    Map.entry("attemptTime", "attemptTime"),
                    Map.entry("createdBy", "createdBy.id"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final LoginAttemptRepository repository;

    private final UserRepository userRepository;

    private final CreatedByResolver createdByResolver;

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
                        .status(true)
                        .reason(reason)
                        .ipAddress(ip)
                        .createdBy(user)
                        .build();

        repository.save(entity);

        log.info(
                "Login attempt saved successfully, userId={}, status={}",
                user != null ? user.getId() : null,
                status
        );
    }

    private BigDecimal resolveCoordinate(
            BigDecimal requestValue,
            HttpServletRequest request,
            String headerName
    ) {

        if (requestValue != null) {
            return requestValue;
        }

        String headerValue =
                request.getHeader(
                        headerName
                );

        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(
                    headerValue.trim()
            );
        } catch (NumberFormatException ex) {
            log.warn(
                    "Invalid coordinate header ignored, headerName={}, value={}",
                    headerName,
                    headerValue
            );
            return null;
        }
    }

    @Override
    public Page<LoginAttemptResponseDTO> getAllLoginAttempts(
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info("Fetch all login attempts service started");

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

        Specification<LoginAttempt> specification =
                DynamicFilterSpecification.build(
                        workingFilters,
                        FILTER_FIELDS
                );

        Specification<LoginAttempt> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

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

    private Specification<LoginAttempt> buildSearchSpecification(
            String search
    ) {

        if (search == null || search.isBlank()) {
            return null;
        }

        String keyword =
                "%"
                        + search.trim().toLowerCase(Locale.ROOT)
                        + "%";

        return (root, query, criteriaBuilder) -> {

            Join<LoginAttempt, User> userJoin =
                    root.join(
                            "user",
                            JoinType.LEFT
                    );

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), keyword),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("reason")), keyword),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ipAddress")), keyword),
                    criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("firstName")), keyword),
                    criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("lastName")), keyword)
            );
        };
    }

    private LoginAttemptResponseDTO mapToDTO(LoginAttempt entity) {

        return LoginAttemptResponseDTO
                .builder()
                .id(entity.getId())
                .name(buildFullName(entity.getUser()))
                .email(entity.getEmail())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .ipAddress(entity.getIpAddress())
                .createdBy(
                        createdByResolver.resolve(
                                entity.getCreatedBy() != null
                                        ? entity.getCreatedBy()
                                        : entity.getUser()
                        )
                )
                .attemptDate(entity.getAttemptDate())
                .attemptTime(entity.getAttemptTime())
                .createdDate(entity.getCreatedDate())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


    private String buildFullName(
            User user
    ) {

        if (user == null) {
            return null;
        }

        String firstName =
                user.getFirstName() == null
                        ? ""
                        : user.getFirstName().trim();

        String lastName =
                user.getLastName() == null
                        ? ""
                        : user.getLastName().trim();

        return (
                firstName
                        + " "
                        + lastName
        ).trim();
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

        User user =
                userRepository
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

        Specification<LoginAttempt> specification =
                DynamicFilterSpecification
                        .<LoginAttempt>equal(
                                "user.id",
                                user.getId()
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        workingFilters,
                                        FILTER_FIELDS
                                )
                        );

        Specification<LoginAttempt> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

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

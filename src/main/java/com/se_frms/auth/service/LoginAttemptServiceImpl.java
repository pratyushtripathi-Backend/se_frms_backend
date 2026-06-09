package com.se_frms.auth.service;



import com.se_frms.auth.dto.LoginAttemptResponseDTO;
import com.se_frms.auth.model.LoginAttempt;

import com.se_frms.auth.repository.LoginAttemptRepository;

import com.se_frms.user.model.User;

import com.se_frms.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl
        implements LoginAttemptService {

    private final LoginAttemptRepository
            repository;

    private final UserRepository userRepository;

    @Transactional(
            propagation =
                    Propagation.REQUIRES_NEW
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

        String ip =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (
                ip == null
                        ||
                        ip.isBlank()
        ) {

            ip =
                    request
                            .getRemoteAddr();
        }

        LoginAttempt entity =

                LoginAttempt
                        .builder()

                        .user(
                                user
                        )

                        .email(
                                email
                        )

                        .attemptStatus(
                                status
                        )

                        .failureReason(
                                reason
                        )

                        .ipAddress(
                                ip
                        )

                        .latitude(
                                latitude
                        )

                        .longitude(
                                longitude
                        )

                        .url(
                                request
                                        .getRequestURL()
                                        .toString()
                        )



                        .build();

        repository.save(
                entity
        );

    }

    @Override
    public List<LoginAttemptResponseDTO> getAllLoginAttempts() {

        return repository

                .findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "attemptedAt"
                        )
                )

                .stream()

                .map(this::mapToDTO)

                .toList();
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
                                ?
                                entity
                                        .getUser()
                                        .getId()
                                :
                                null
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
    public List<LoginAttemptResponseDTO>
    getLoginAttemptsByUserId(

            UUID userId

    ) {

        User user = userRepository
                .findById(userId)

                .orElseThrow(

                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        return repository

                .findByUserIdOrderByAttemptedAtDesc(
                        user.getId()
                )

                .stream()

                .map(this::mapToDTO)

                .toList();
    }
}

package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginHistoryResponseDTO;
import com.se_frms.auth.model.LoginHistory;
import com.se_frms.auth.repository.LoginHistoryRepository;
import com.se_frms.user.model.User;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Integer;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl
        implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public void saveLoginHistory(
            User user,
            HttpServletRequest request,
            Boolean status
    ) {

        log.info(
                "Saving login history, userId={}, status={}",
                user.getId(),
                status
        );

        String ipAddress =
                extractIpAddress(
                        request
                );

        LoginHistory loginHistory =
                LoginHistory
                        .builder()
                        .user(
                                user
                        )
                        .ipAddress(
                                ipAddress
                        )
                        .latitude(
                                null
                        )
                        .longitude(
                                null
                        )
                        .url(
                                request
                                        .getRequestURL()
                                        .toString()
                        )
                        .status(
                                status
                        )
                        .createdBy(
                                user
                        )
                        .build();

        loginHistoryRepository
                .save(
                        loginHistory
                );

        log.info(
                "Login history saved successfully, userId={}, status={}",
                user.getId(),
                status
        );
    }

    private String extractIpAddress(
            HttpServletRequest request
    ) {

        String forwardedIp =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (forwardedIp != null && !forwardedIp.isBlank()) {
            return forwardedIp
                    .split(",")[0]
                    .trim();
        }

        String realIp =
                request.getHeader(
                        "X-Real-IP"
                );

        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request
                .getRemoteAddr();
    }

    @Override
    public List<LoginHistoryResponseDTO>
    getLoginHistory(
            User user
    ) {

        log.info(
                "Fetch login history service started, userId={}",
                user.getId()
        );

        List<LoginHistoryResponseDTO> response =
                loginHistoryRepository
                        .findByUserOrderByCreatedDateDesc(
                                user
                        )
                        .stream()
                        .map(this::mapToDTO)
                        .toList();

        log.info(
                "Login history fetched successfully, userId={}, count={}",
                user.getId(),
                response.size()
        );

        return response;
    }

    @Override
    public List<LoginHistoryResponseDTO>
    getLoginHistoryByUserId(

            Integer userId

    ) {

        log.info(
                "Fetch login history by userId service started, userId={}",
                userId
        );

        List<LoginHistoryResponseDTO> response =
                loginHistoryRepository
                        .findByUserIdOrderByCreatedDateDesc(
                                userId
                        )
                        .stream()
                        .map(this::mapToDTO)
                        .toList();

        log.info(
                "Login history by userId fetched successfully, userId={}, count={}",
                userId,
                response.size()
        );

        return response;
    }

    private LoginHistoryResponseDTO mapToDTO(
            LoginHistory history
    ) {

        return LoginHistoryResponseDTO
                .builder()
                .id(
                        history.getId()
                )
                .userId(
                        String.valueOf(
                                history
                                        .getUser()
                                        .getId()
                        )
                )
                .loginDate(
                        history.getLoginDate()
                )
                .loginTime(
                        history.getLoginTime()
                )
                .ipAddress(
                        history.getIpAddress()
                )
                .latitude(
                        history.getLatitude()
                )
                .longitude(
                        history.getLongitude()
                )
                .url(
                        history.getUrl()
                )
                .status(
                        history.getStatus()
                )
                .createdDate(
                        history.getCreatedDate()
                )
                .updatedAt(
                        history.getUpdatedAt()
                )
                .build();
    }
}
package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginHistoryResponseDTO;
import com.se_frms.auth.model.LoginHistory;
import com.se_frms.auth.repository.LoginHistoryRepository;

import com.se_frms.user.model.User;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Integer;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl
        implements LoginHistoryService {

    private final LoginHistoryRepository
            loginHistoryRepository;

    @Override
    public void saveLoginHistory(

            User user,

            HttpServletRequest request,

            Boolean status

    ) {

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
    }

    private String extractIpAddress(

            HttpServletRequest request

    ) {

        String forwardedIp =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (

                forwardedIp != null

                        &&

                        !forwardedIp.isBlank()

        ) {

            return forwardedIp
                    .split(",")[0]
                    .trim();
        }

        String realIp =
                request.getHeader(
                        "X-Real-IP"
                );

        if (

                realIp != null

                        &&

                        !realIp.isBlank()

        ) {

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

        return loginHistoryRepository

                .findByUserOrderByCreatedDateDesc(
                        user
                )

                .stream()

                .map(

                        history ->

                                LoginHistoryResponseDTO
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

                                        .build()

                )

                .toList();
    }

    @Override
    public List<LoginHistoryResponseDTO>
    getLoginHistoryByUserId(

            Integer userId

    ) {

        return loginHistoryRepository

                .findByUserIdOrderByCreatedDateDesc(
                        userId
                )

                .stream()

                .map(

                        history ->

                                LoginHistoryResponseDTO
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

                                        .build()

                )

                .toList();
    }
}
package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginHistoryResponseDTO;
import com.se_frms.auth.model.LoginHistory;
import com.se_frms.auth.repository.LoginHistoryRepository;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.security.XssUtil;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.user.model.User;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.lang.Integer;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl
        implements LoginHistoryService {

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("userId", "user.id"),
                    Map.entry("email", "user.email"),
                    Map.entry("firstName", "user.firstName"),
                    Map.entry("lastName", "user.lastName"),
                    Map.entry("loginDate", "loginDate"),
                    Map.entry("loginTime", "loginTime"),
                    Map.entry("ipAddress", "ipAddress"),
                    Map.entry("macAddress", "macAddress"),
                    Map.entry("latitude", "latitude"),
                    Map.entry("longitude", "longitude"),
                    Map.entry("url", "url"),
                    Map.entry("status", "status"),
                    Map.entry("createdBy", "createdBy.id"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final LoginHistoryRepository loginHistoryRepository;
    private final CreatedByResolver createdByResolver;

    @Override
    public void saveLoginHistory(
            User user,
            HttpServletRequest request,
            Boolean status,
            String macAddress,
            BigDecimal latitude,
            BigDecimal longitude
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

        String resolvedMacAddress =
                extractMacAddress(
                        macAddress,
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
                        .macAddress(
                                resolvedMacAddress
                        )
                        .latitude(
                                resolveCoordinate(
                                        latitude,
                                        request,
                                        "X-Client-Latitude"
                                )
                        )
                        .longitude(
                                resolveCoordinate(
                                        longitude,
                                        request,
                                        "X-Client-Longitude"
                                )
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

    private Double resolveCoordinate(
            BigDecimal requestValue,
            HttpServletRequest request,
            String headerName
    ) {

        if (requestValue != null) {
            return requestValue.doubleValue();
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
            ).doubleValue();
        } catch (NumberFormatException ex) {
            log.warn(
                    "Invalid coordinate header ignored, headerName={}, value={}",
                    headerName,
                    headerValue
            );
            return null;
        }
    }

    private String extractMacAddress(
            String macAddress,
            HttpServletRequest request
    ) {

        String resolvedMacAddress =
                macAddress;

        if (resolvedMacAddress == null || resolvedMacAddress.isBlank()) {
            resolvedMacAddress =
                    request.getHeader(
                            "X-Mac-Address"
                    );
        }

        if (resolvedMacAddress == null || resolvedMacAddress.isBlank()) {
            resolvedMacAddress =
                    request.getHeader(
                            "X-Device-Mac"
                    );
        }

        if (resolvedMacAddress == null || resolvedMacAddress.isBlank()) {
            resolvedMacAddress =
                    request.getHeader(
                            "Mac-Address"
                    );
        }

        if (resolvedMacAddress == null || resolvedMacAddress.isBlank()) {
            return null;
        }

        return XssUtil
                .clean(
                        resolvedMacAddress.trim()
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
    public Page<LoginHistoryResponseDTO>
    getLoginHistory(
            User user,
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info(
                "Fetch login history service started, userId={}",
                user.getId()
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

        Specification<LoginHistory> specification =
                DynamicFilterSpecification
                        .<LoginHistory>equal(
                                "user.id",
                                user.getId()
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        workingFilters,
                                        FILTER_FIELDS
                                )
                        );

        Specification<LoginHistory> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

        Page<LoginHistoryResponseDTO> response =
                loginHistoryRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToDTO);

        log.info(
                "Login history fetched successfully, userId={}, count={}",
                user.getId(),
                response.getNumberOfElements()
        );

        return response;
    }

    @Override
    public Page<LoginHistoryResponseDTO> getAllLoginHistory(
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info("Fetch all login history service started");

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

        Specification<LoginHistory> specification =
                DynamicFilterSpecification.build(
                        workingFilters,
                        FILTER_FIELDS
                );

        Specification<LoginHistory> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

        Page<LoginHistoryResponseDTO> response =
                loginHistoryRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToDTO);

        log.info(
                "All login history fetched successfully, count={}",
                response.getNumberOfElements()
        );

        return response;
    }

    @Override
    public Page<LoginHistoryResponseDTO>
    getLoginHistoryByUserId(
            Integer userId,
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info(
                "Fetch login history by userId service started, userId={}",
                userId
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

        Specification<LoginHistory> specification =
                DynamicFilterSpecification
                        .<LoginHistory>equal(
                                "user.id",
                                userId
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        workingFilters,
                                        FILTER_FIELDS
                                )
                        );

        Specification<LoginHistory> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

        Page<LoginHistoryResponseDTO> response =
                loginHistoryRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToDTO);

        log.info(
                "Login history by userId fetched successfully, userId={}, count={}",
                userId,
                response.getNumberOfElements()
        );

        return response;
    }


    private Specification<LoginHistory> buildSearchSpecification(
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
                                criteriaBuilder.lower(root.get("user").get("email")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("user").get("firstName")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("user").get("lastName")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("ipAddress")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("macAddress")),
                                keyword
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("url")),
                                keyword
                        )
                );
    }

    private LoginHistoryResponseDTO mapToDTO(
            LoginHistory history
    ) {

        return LoginHistoryResponseDTO
                .builder()
                .id(
                        history.getId()
                )
                .name(
                        buildFullName(
                                history.getUser()
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
                .macAddress(
                        history.getMacAddress()
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
                .createdBy(
                        createdByResolver.resolve(history.getCreatedBy())
                )
                .createdDate(
                        history.getCreatedDate()
                )
                .updatedAt(
                        history.getUpdatedAt()
                )
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

        return (firstName + " " + lastName).trim();
    }
}

package com.se_frms.blackListUser.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.blackListUser.dto.BlackListUserRequestDTO;
import com.se_frms.blackListUser.dto.BlackListUserResponseDTO;
import com.se_frms.blackListUser.dto.RemoveBlackListRequestDTO;
import com.se_frms.blackListUser.model.BlackListUser;
import com.se_frms.blackListUser.repository.BlackListUserRepository;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.common.security.XssUtil;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BlackListUserServiceImpl
        implements BlackListUserService {

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("userId", "user.id"),
                    Map.entry("employeeName", "employeeName"),
                    Map.entry("email", "email"),
                    Map.entry("mobile", "mobile"),
                    Map.entry("status", "status"),
                    Map.entry("reason", "reason"),
                    Map.entry("riskType", "riskType"),
                    Map.entry("createdBy", "createdBy.id"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final BlackListUserRepository blackListUserRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final CreatedByResolver createdByResolver;

    @Override
    public BlackListUserResponseDTO blackListUser(
            BlackListUserRequestDTO request
    ) {

        log.info("Blacklist user service started, userId={}", request.getUserId());

        User user =
                getUserOrThrow(
                        request.getUserId()
                );

        if (blackListUserRepository.existsByUserIdAndStatus(
                user.getId(),
                true
        )) {
            log.warn("Blacklist user failed because user is already blacklisted, userId={}", user.getId());
            throw new InvalidRequestException("User already blacklisted");
        }

        User loggedInAdmin =
                currentUserService.getCurrentUser();

        BlackListUser blackListUser =
                BlackListUser
                        .builder()
                        .user(user)
                        .employeeName(buildEmployeeName(user))
                        .email(user.getEmail())
                        .mobile(user.getPhoneNumber())
                        .status(true)
                        .reason(clean(request.getReason()))
                        .riskType(clean(request.getRiskType()))
                        .createdBy(loggedInAdmin)
                        .build();

        BlackListUser savedBlackListUser =
                blackListUserRepository.save(blackListUser);

        user.setStatus(false);
        userRepository.save(user);

        log.info("User blacklisted successfully, userId={}", user.getId());

        return mapToResponse(savedBlackListUser);
    }

    @Override
    public BlackListUserResponseDTO removeBlackList(
            RemoveBlackListRequestDTO request
    ) {

        log.info("Remove blacklist service started, userId={}", request.getUserId());

        BlackListUser blackListUser =
                blackListUserRepository
                        .findTopByUserIdAndStatusOrderByCreatedDateDesc(
                                request.getUserId(),
                                true
                        )
                        .orElseThrow(() -> {
                            log.warn("Remove blacklist failed because active blacklist was not found, userId={}", request.getUserId());
                            return new InvalidRequestException("User is not blacklisted");
                        });

        User user =
                blackListUser.getUser();

        blackListUser.setStatus(false);
        BlackListUser savedBlackListUser =
                blackListUserRepository.save(blackListUser);

        user.setStatus(true);
        userRepository.save(user);

        log.info("User blacklist removed successfully, userId={}", user.getId());

        return mapToResponse(savedBlackListUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlackListUserResponseDTO> getAllBlackListUsers(
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info(
                "Fetch all blacklist users service started, page={}, size={}",
                page,
                size
        );

        Map<String, String> blackListFilters =
                new HashMap<>(
                        filters
                );

        String search =
                blackListFilters.remove(
                        "search"
                );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        blackListFilters,
                        FILTER_FIELDS,
                        "employeeName",
                        Sort.Direction.ASC
                );

        Specification<BlackListUser> specification =
                DynamicFilterSpecification
                        .<BlackListUser>equal(
                                "status",
                                true
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        blackListFilters,
                                        FILTER_FIELDS
                                )
                        )
                        .and(
                                buildBlackListUserSearchSpecification(
                                        search
                                )
                        );

        Page<BlackListUserResponseDTO> response =
                blackListUserRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToResponse);

        log.info(
                "Blacklist users fetched successfully, count={}",
                response.getNumberOfElements()
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BlackListUserResponseDTO getBlackListUserById(
            Integer id
    ) {

        log.info("Fetch blacklist user by id service started, id={}", id);

        BlackListUser blackListUser =
                blackListUserRepository
                        .findById(id)
                        .orElseThrow(() -> {
                            log.warn("Fetch blacklist user failed because record was not found, id={}", id);
                            return new InvalidRequestException("Blacklist user record not found");
                        });

        log.info("Blacklist user fetched successfully, id={}", id);

        return mapToResponse(blackListUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlackListUserResponseDTO> getBlackListUsersByUserId(
            Integer userId,
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info(
                "Fetch blacklist users by userId service started, userId={}, page={}, size={}",
                userId,
                page,
                size
        );

        Map<String, String> blackListFilters =
                new HashMap<>(
                        filters
                );

        String search =
                blackListFilters.remove(
                        "search"
                );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        blackListFilters,
                        FILTER_FIELDS,
                        "employeeName",
                        Sort.Direction.ASC
                );

        Specification<BlackListUser> specification =
                DynamicFilterSpecification
                        .<BlackListUser>equal(
                                "user.id",
                                userId
                        )
                        .and(
                                DynamicFilterSpecification.equal(
                                        "status",
                                        true
                                )
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        blackListFilters,
                                        FILTER_FIELDS
                                )
                        )
                        .and(
                                buildBlackListUserSearchSpecification(
                                        search
                                )
                        );

        Page<BlackListUserResponseDTO> response =
                blackListUserRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToResponse);

        log.info(
                "Blacklist users by userId fetched successfully, userId={}, count={}",
                userId,
                response.getNumberOfElements()
        );

        return response;
    }

    private Specification<BlackListUser> buildBlackListUserSearchSpecification(
            String search
    ) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern =
                    "%"
                            + search
                            .trim()
                            .toLowerCase(Locale.ROOT)
                            + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("employeeName")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("email")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("mobile")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("reason")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("riskType")),
                            pattern
                    )
            );
        };
    }

    private User getUserOrThrow(
            Integer userId
    ) {

        return userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User lookup failed because user was not found, userId={}", userId);
                    return new InvalidRequestException("User not found");
                });
    }

    private String buildEmployeeName(
            User user
    ) {

        String firstName =
                user.getFirstName() == null
                        ? ""
                        : user.getFirstName();

        String lastName =
                user.getLastName() == null
                        ? ""
                        : user.getLastName();

        return (firstName + " " + lastName).trim();
    }

    private String clean(
            String value
    ) {

        if (value == null) {
            return null;
        }

        return XssUtil.clean(value.trim());
    }

    private BlackListUserResponseDTO mapToResponse(
            BlackListUser blackListUser
    ) {

        return BlackListUserResponseDTO
                .builder()
                .id(blackListUser.getId())
                .userId(blackListUser.getUser().getId())
                .employeeName(blackListUser.getEmployeeName())
                .email(blackListUser.getEmail())
                .mobile(blackListUser.getMobile())
                .status(blackListUser.getStatus())
                .reason(blackListUser.getReason())
                .riskType(blackListUser.getRiskType())
                .createdBy(
                        createdByResolver.resolve(blackListUser.getCreatedBy())
                )
                .createdDate(blackListUser.getCreatedDate())
                .updatedAt(blackListUser.getUpdatedAt())
                .build();
    }
}

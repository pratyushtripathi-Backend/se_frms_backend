package com.se_frms.userRole.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import com.se_frms.userRole.dto.UserRoleRequestDTO;
import com.se_frms.userRole.dto.UserRoleResponseDTO;
import com.se_frms.userRole.model.UserRole;
import com.se_frms.userRole.repository.UserRoleRepository;
import com.se_frms.common.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.se_frms.userRole.dto.UserRoleStatusRequestDTO;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl
        implements UserRoleService {

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("userId", "user.id"),
                    Map.entry("firstName", "user.firstName"),
                    Map.entry("lastName", "user.lastName"),
                    Map.entry("email", "user.email"),
                    Map.entry("roleId", "role.roleId"),
                    Map.entry("roleName", "role.roleName"),
                    Map.entry("status", "status"),
                    Map.entry("createdBy", "createdBy"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final UserRepository userRepository;
    private final RoleMasterRepository roleMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final CurrentUserService currentUserService;
    private final CreatedByResolver createdByResolver;
    @Override
    public UserRoleResponseDTO assignRole(
            UserRoleRequestDTO request
    ) {

        log.info(
                "Assign role service started, userId={}",
                request.getUserId()
        );

        User user =
                userRepository.findById(request.getUserId())
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Assign role failed because user was not found, userId={}",
                                            request.getUserId()
                                    );

                                    return new InvalidRequestException(
                                            "User not found"
                                    );
                                }
                        );

        RoleMaster roleMaster =
                getActiveRoleMaster(request.getRoleName());

        LocalDateTime now = LocalDateTime.now();

        userRoleRepository.findByUserAndStatus(user, true)
                .forEach(existingRole -> {
                    existingRole.setStatus(false);
                    existingRole.setUpdatedAt(now);
                    userRoleRepository.save(existingRole);
                });

        Integer loggedInAdminId =
                currentUserService.getCurrentUserId();

        UserRole userRole =
                userRoleRepository
                        .findByUserAndRole(user, roleMaster)
                        .orElse(
                                UserRole.builder()
                                        .user(user)
                                        .role(roleMaster)
                                        .createdBy(loggedInAdminId)
                                        .build()
                        );

        if (userRole.getCreatedBy() == null) {
            userRole.setCreatedBy(loggedInAdminId);
        }

        userRole.setStatus(true);
        userRole.setUpdatedAt(now);

        UserRole savedUserRole =
                userRoleRepository.save(userRole);

        user.setUserType(roleMaster.getRoleName());
        user.setUpdatedAt(now);
        userRepository.save(user);

        log.info(
                "Role assigned successfully, userId={}, roleName={}",
                user.getId(),
                roleMaster.getRoleName()
        );

        return mapToResponse(savedUserRole);
    }

    @Override
    public Page<UserRoleResponseDTO> getAllUserRoles(
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info(
                "Fetch all user roles service started, page={}, size={}",
                page,
                size
        );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        filters,
                        FILTER_FIELDS,
                        "createdDate",
                        Sort.Direction.DESC
                );

        Specification<UserRole> specification =
                DynamicFilterSpecification.build(
                        filters,
                        FILTER_FIELDS
                );

        Page<UserRoleResponseDTO> responseData =
                userRoleRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::mapToResponse);

        log.info(
                "User roles fetched successfully, count={}",
                responseData.getNumberOfElements()
        );

        return responseData;
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserRoleResponseDTO> getActiveUserRoles() {

        log.info("Fetch active user roles service started");

        List<UserRoleResponseDTO> response =
                userRoleRepository
                        .findByStatusOrderByUserFirstNameAscUserLastNameAsc(
                                true
                        )
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        log.info("Active user roles fetched successfully, count={}", response.size());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleResponseDTO> getRolesByUser(
            Integer userId
    ) {

        log.info(
                "Fetch roles by user service started, userId={}",
                userId
        );

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Fetch roles by user failed because user was not found, userId={}",
                                            userId
                                    );

                                    return new InvalidRequestException(
                                            "User not found"
                                    );
                                }
                        );

        List<UserRoleResponseDTO> response =
                userRoleRepository
                        .findByUserOrderByRoleRoleNameAsc(user)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        log.info(
                "Roles by user fetched successfully, userId={}, count={}",
                userId,
                response.size()
        );

        return response;
    }

    @Override
    public UserRoleResponseDTO updateStatus(
            Integer id,
            UserRoleStatusRequestDTO request
    ) {

        log.info(
                "Update user role service started, userRoleId={}, status={}, roleName={}",
                id,
                request.getStatus(),
                request.getRoleName()
        );

        UserRole userRole =
                userRoleRepository.findById(id)
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Update user role failed because user role was not found, userRoleId={}",
                                            id
                                    );

                                    return new InvalidRequestException(
                                            "User role not found"
                                    );
                                }
                        );

        User user =
                userRole.getUser();

        Integer loggedInAdminId =
                currentUserService.getCurrentUserId();

        LocalDateTime now = LocalDateTime.now();

        if (request.getRoleName() != null
                && !request.getRoleName().isBlank()) {

            RoleMaster roleMaster =
                    getActiveRoleMaster(request.getRoleName());

            UserRole targetUserRole =
                    userRoleRepository
                            .findByUserAndRole(
                                    user,
                                    roleMaster
                            )
                            .orElse(userRole);

            for (UserRole existingRole :
                    userRoleRepository.findByUserAndStatus(user, true)) {

                if (!existingRole.getId().equals(targetUserRole.getId())) {
                    existingRole.setStatus(false);
                    existingRole.setUpdatedAt(now);
                    userRoleRepository.save(existingRole);
                }
            }

            if (!targetUserRole.getId().equals(userRole.getId())) {
                userRole.setStatus(false);
                userRole.setUpdatedAt(now);
                userRoleRepository.save(userRole);
            }

            targetUserRole.setRole(roleMaster);
            targetUserRole.setStatus(
                    request.getStatus() != null
                            ? request.getStatus()
                            : true
            );

            if (targetUserRole.getCreatedBy() == null) {
                targetUserRole.setCreatedBy(loggedInAdminId);
            }

            targetUserRole.setUpdatedAt(now);

            UserRole savedUserRole =
                    userRoleRepository.save(targetUserRole);

            if (Boolean.TRUE.equals(savedUserRole.getStatus())) {
                user.setUserType(roleMaster.getRoleName());
                user.setUpdatedAt(now);
                userRepository.save(user);
            }

            log.info(
                    "User role updated successfully, userId={}, roleName={}, status={}",
                    user.getId(),
                    roleMaster.getRoleName(),
                    savedUserRole.getStatus()
            );

            return mapToResponse(savedUserRole);
        }

        userRole.setStatus(request.getStatus());

        if (userRole.getCreatedBy() == null) {
            userRole.setCreatedBy(loggedInAdminId);
        }

        userRole.setUpdatedAt(now);

        UserRole savedUserRole =
                userRoleRepository.save(userRole);

        log.info(
                "User role status updated successfully, userRoleId={}, status={}",
                savedUserRole.getId(),
                savedUserRole.getStatus()
        );

        return mapToResponse(savedUserRole);
    }

    private RoleMaster getActiveRoleMaster(
            String roleName
    ) {

        String normalizedRoleName =
                roleName.trim().toUpperCase();

        return roleMasterRepository
                .findByRoleNameAndStatus(
                        normalizedRoleName,
                        true
                )
                .orElseThrow(
                        () -> {
                            log.warn(
                                    "Role lookup failed because role is invalid or inactive, roleName={}",
                                    normalizedRoleName
                            );

                            return new InvalidRequestException(
                                    "Invalid or inactive role"
                            );
                        }
                );
    }

    private UserRoleResponseDTO mapToResponse(
            UserRole userRole
    ) {

        User user = userRole.getUser();

        return UserRoleResponseDTO.builder()
                .id(userRole.getId())
                .userId(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .roleId(userRole.getRole().getRoleId())
                .roleName(userRole.getRole().getRoleName())
                .status(userRole.getStatus())
                .createdBy(createdByResolver.resolve(userRole.getCreatedBy()))
                .createdDate(userRole.getCreatedDate())
                .updatedAt(userRole.getUpdatedAt())
                .build();
    }
}

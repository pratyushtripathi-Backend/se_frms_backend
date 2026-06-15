package com.se_frms.userRole.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import com.se_frms.user.enums.Role;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl
        implements UserRoleService {

    private final UserRepository userRepository;
    private final RoleMasterRepository roleMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final CurrentUserService currentUserService;
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

        Role selectedRole =
                convertRoleMasterToEnum(roleMaster);

        userRoleRepository.findByUserAndStatus(user, true)
                .forEach(existingRole -> {
                    existingRole.setStatus(false);
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

        UserRole savedUserRole =
                userRoleRepository.save(userRole);

        user.setUserType(selectedRole.name());
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
            int size
    ) {

        log.info(
                "Fetch all user roles service started, page={}, size={}",
                page,
                size
        );

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id").ascending()
                );

        Page<UserRoleResponseDTO> responseData =
                userRoleRepository
                        .findAll(pageable)
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
                userRoleRepository.findByStatus(true)
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
                userRoleRepository.findByUser(user)
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
            Boolean status
    ) {

        log.info(
                "Update user role status service started, userRoleId={}, status={}",
                id,
                status
        );

        UserRole userRole =
                userRoleRepository.findById(id)
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Update user role status failed because user role was not found, userRoleId={}",
                                            id
                                    );

                                    return new InvalidRequestException(
                                            "User role not found"
                                    );
                                }
                        );

        userRole.setStatus(status);

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

    private Role convertRoleMasterToEnum(
            RoleMaster roleMaster
    ) {

        try {
            return Role.valueOf(roleMaster.getRoleName());
        } catch (IllegalArgumentException ex) {
            log.warn(
                    "Role conversion failed because role is not configured in application, roleName={}",
                    roleMaster.getRoleName()
            );

            throw new InvalidRequestException(
                    "Role is not configured in application"
            );
        }
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
                .createdDate(userRole.getCreatedDate())
                .updatedAt(userRole.getUpdatedAt())
                .build();
    }
}

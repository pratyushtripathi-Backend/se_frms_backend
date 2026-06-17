package com.se_frms.roleMaster.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.roleMaster.dto.RoleMasterRequestDTO;
import com.se_frms.roleMaster.dto.RoleMasterResponseDTO;
import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import com.se_frms.common.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import com.se_frms.common.util.PaginationUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleMasterServiceImpl
        implements RoleMasterService {
    private final CurrentUserService currentUserService;
    private static final Set<String> ALLOWED_ROLES =
            Set.of("ADMIN", "EMPLOYEE", "USER");

    private final RoleMasterRepository roleMasterRepository;

    @Override
    public RoleMasterResponseDTO createRole(
            RoleMasterRequestDTO request
    ) {

        log.info("Create role service started");

        String roleName = normalizeRoleName(request.getRoleName());

        validateAllowedRole(roleName);

        if (roleMasterRepository.existsByRoleName(roleName)) {
            log.warn("Create role failed because role already exists, roleName={}", roleName);
            throw new InvalidRequestException("Role already exists");
        }
        Integer loggedInAdminId =
                currentUserService.getCurrentUserId();
        RoleMaster roleMaster =
                RoleMaster.builder()
                        .roleName(roleName)
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : true
                        )
                        .createdBy(loggedInAdminId)
                        .build();

        RoleMaster savedRole =
                roleMasterRepository.save(roleMaster);

        log.info(
                "Role created successfully, roleId={}, roleName={}",
                savedRole.getRoleId(),
                savedRole.getRoleName()
        );

        return mapToResponse(savedRole);
    }

    @Override
    public Page<RoleMasterResponseDTO> getAllRoles(
            Integer page,
            Integer size
    ) {

        Pageable pageable =
                PaginationUtil.createPageable(
                        page,
                        size,
                        Sort.by("roleId").ascending()
                );

        return roleMasterRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleMasterResponseDTO> getActiveRoles() {

        log.info("Fetch active roles service started");

        List<RoleMasterResponseDTO> response =
                roleMasterRepository.findByStatus(true)
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        log.info("Active roles fetched successfully, count={}", response.size());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleMasterResponseDTO getRoleById(
            Integer roleId
    ) {

        log.info("Fetch role by id service started, roleId={}", roleId);

        RoleMaster roleMaster =
                roleMasterRepository.findById(roleId)
                        .orElseThrow(
                                () -> {
                                    log.warn("Fetch role failed because role was not found, roleId={}", roleId);
                                    return new InvalidRequestException(
                                            "Role not found"
                                    );
                                }
                        );

        log.info("Role fetched successfully, roleId={}", roleId);

        return mapToResponse(roleMaster);
    }

    @Override
    public RoleMasterResponseDTO updateRole(
            Integer roleId,
            RoleMasterRequestDTO request
    ) {

        log.info("Update role service started, roleId={}", roleId);

        RoleMaster roleMaster =
                roleMasterRepository.findById(roleId)
                        .orElseThrow(
                                () -> {
                                    log.warn("Update role failed because role was not found, roleId={}", roleId);
                                    return new InvalidRequestException(
                                            "Role not found"
                                    );
                                }
                        );

        String roleName = normalizeRoleName(request.getRoleName());

        validateAllowedRole(roleName);

        roleMasterRepository.findByRoleName(roleName)
                .ifPresent(existingRole -> {
                    if (!existingRole.getRoleId().equals(roleId)) {
                        log.warn(
                                "Update role failed because role already exists, roleId={}, roleName={}",
                                roleId,
                                roleName
                        );

                        throw new InvalidRequestException(
                                "Role already exists"
                        );
                    }
                });

        roleMaster.setRoleName(roleName);

        if (request.getStatus() != null) {
            roleMaster.setStatus(request.getStatus());
        }

        RoleMaster savedRole =
                roleMasterRepository.save(roleMaster);

        log.info(
                "Role updated successfully, roleId={}, roleName={}",
                savedRole.getRoleId(),
                savedRole.getRoleName()
        );

        return mapToResponse(savedRole);
    }

    private String normalizeRoleName(
            String roleName
    ) {

        return roleName.trim().toUpperCase();
    }

    private void validateAllowedRole(
            String roleName
    ) {

        if (!ALLOWED_ROLES.contains(roleName)) {
            log.warn("Role validation failed because role is not allowed, roleName={}", roleName);

            throw new InvalidRequestException(
                    "Only ADMIN, EMPLOYEE and USER roles are allowed"
            );
        }
    }

    private RoleMasterResponseDTO mapToResponse(
            RoleMaster roleMaster
    ) {

        return RoleMasterResponseDTO.builder()
                .roleId(roleMaster.getRoleId())
                .roleName(roleMaster.getRoleName())
                .status(roleMaster.getStatus())
                .createdBy(roleMaster.getCreatedBy())
                .createdDate(roleMaster.getCreatedDate())
                .updatedAt(roleMaster.getUpdatedAt())
                .build();
    }
}
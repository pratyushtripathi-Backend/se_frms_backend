package com.se_frms.roleMaster.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.roleMaster.dto.RoleMasterRequestDTO;
import com.se_frms.roleMaster.dto.RoleMasterResponseDTO;
import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleMasterServiceImpl
        implements RoleMasterService {

    private static final Set<String> ALLOWED_ROLES =
            Set.of("ADMIN", "EMPLOYEE", "USER");

    private final RoleMasterRepository roleMasterRepository;

    @Override
    public RoleMasterResponseDTO createRole(
            RoleMasterRequestDTO request
    ) {

        String roleName = normalizeRoleName(request.getRoleName());

        validateAllowedRole(roleName);

        if (roleMasterRepository.existsByRoleName(roleName)) {
            throw new InvalidRequestException("Role already exists");
        }

        RoleMaster roleMaster =
                RoleMaster.builder()
                        .roleName(roleName)
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : true
                        )
                        .createdBy(request.getCreatedBy())
                        .build();

        return mapToResponse(roleMasterRepository.save(roleMaster));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleMasterResponseDTO> getAllRoles() {

        return roleMasterRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleMasterResponseDTO> getActiveRoles() {

        return roleMasterRepository.findByStatus(true)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleMasterResponseDTO getRoleById(Integer roleId) {

        RoleMaster roleMaster =
                roleMasterRepository.findById(roleId)
                        .orElseThrow(
                                () -> new InvalidRequestException(
                                        "Role not found"
                                )
                        );

        return mapToResponse(roleMaster);
    }

    @Override
    public RoleMasterResponseDTO updateRole(
            Integer roleId,
            RoleMasterRequestDTO request
    ) {

        RoleMaster roleMaster =
                roleMasterRepository.findById(roleId)
                        .orElseThrow(
                                () -> new InvalidRequestException(
                                        "Role not found"
                                )
                        );

        String roleName = normalizeRoleName(request.getRoleName());

        validateAllowedRole(roleName);

        roleMasterRepository.findByRoleName(roleName)
                .ifPresent(existingRole -> {
                    if (!existingRole.getRoleId().equals(roleId)) {
                        throw new InvalidRequestException(
                                "Role already exists"
                        );
                    }
                });

        roleMaster.setRoleName(roleName);

        if (request.getStatus() != null) {
            roleMaster.setStatus(request.getStatus());
        }

        return mapToResponse(roleMasterRepository.save(roleMaster));
    }

    private String normalizeRoleName(String roleName) {
        return roleName.trim().toUpperCase();
    }

    private void validateAllowedRole(String roleName) {
        if (!ALLOWED_ROLES.contains(roleName)) {
            throw new InvalidRequestException(
                    "Only ADMIN, EMPLOYEE and USER roles are allowed"
            );
        }
    }

    private RoleMasterResponseDTO mapToResponse(RoleMaster roleMaster) {

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
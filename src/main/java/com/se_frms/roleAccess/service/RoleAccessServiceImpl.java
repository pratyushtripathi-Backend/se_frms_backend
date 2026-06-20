package com.se_frms.roleAccess.service;

import com.se_frms.access.model.AccessMaster;
import com.se_frms.access.repository.AccessMasterRepository;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.roleAccess.dto.RoleAccessRequestDTO;
import com.se_frms.roleAccess.dto.RoleAccessResponseDTO;
import com.se_frms.roleAccess.model.RoleAccess;
import com.se_frms.roleAccess.repository.RoleAccessRepository;
import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleAccessServiceImpl
        implements RoleAccessService {

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("roleId", "role.roleId"),
                    Map.entry("roleName", "role.roleName"),
                    Map.entry("accessId", "access.id"),
                    Map.entry("accessName", "access.accessName"),
                    Map.entry("status", "status"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final RoleAccessRepository repository;

    private final RoleMasterRepository roleRepository;

    private final AccessMasterRepository accessRepository;

    @Override
    public RoleAccessResponseDTO create(

            RoleAccessRequestDTO request

    ) {

        RoleMaster role =

                roleRepository

                        .findById(
                                request.getRoleId()
                        )

                        .orElseThrow(

                                () ->

                                        new RuntimeException(
                                                "Role not found"
                                        )

                        );

        for (

                Integer accessId

                :

                request.getAccessIds()

        ) {

            AccessMaster access =

                    accessRepository

                            .findById(
                                    accessId
                            )

                            .orElseThrow(

                                    () ->

                                            new RuntimeException(
                                                    "Access not found"
                                            )

                            );

            if (

                    repository

                            .existsByRoleRoleIdAndAccessId(

                                    role.getRoleId(),

                                    accessId

                            )

            ) {

                continue;

            }

            repository.save(

                    RoleAccess
                            .builder()

                            .role(
                                    role
                            )

                            .access(
                                    access
                            )

                            .status(
                                    true
                            )

                            .createdDate(
                                    LocalDateTime.now()
                            )

                            .build()

            );

        }

        return getByRole(
                role.getRoleId()
        );

    }

    @Override
    public Page<RoleAccessResponseDTO> getAll(
            int page,
            int size,
            Map<String, String> filters
    ) {

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        filters,
                        FILTER_FIELDS,
                        "role.roleName",
                        Sort.Direction.ASC
                );

        Specification<RoleAccess> specification =
                DynamicFilterSpecification
                        .<RoleAccess>equal(
                                "status",
                                true
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        filters,
                                        FILTER_FIELDS
                                )
                        );

        return repository

                .findAll(
                        specification,
                        pageable
                )

                .map(

                        v ->

                                RoleAccessResponseDTO

                                        .builder()

                                        .roleId(

                                                v.getRole()

                                                        .getRoleId()

                                        )

                                        .roleName(

                                                v.getRole()

                                                        .getRoleName()

                                        )

                                        .accessNames(

                                                List.of(

                                                        v.getAccess()

                                                                .getAccessName()

                                                )

                                        )

                                        .build()

                );

    }

    @Override
    public RoleAccessResponseDTO getByRole(

            Integer roleId

    ) {

        List<RoleAccess> mappings =

                repository

                        .findByRoleRoleIdAndStatusTrueOrderByAccessAccessNameAsc(
                                roleId
                        );

        if (

                mappings.isEmpty()

        ) {

            throw new RuntimeException(

                    "No active access found"

            );

        }

        return RoleAccessResponseDTO

                .builder()

                .roleId(
                        roleId
                )

                .roleName(

                        mappings

                                .get(0)

                                .getRole()

                                .getRoleName()

                )

                .accessNames(

                        mappings

                                .stream()

                                .map(

                                        x ->

                                                x.getAccess()

                                                        .getAccessName()

                                )

                                .toList()

                )

                .build();

    }

    @Override
    public String updateAccessStatus(

            Integer roleId,

            Integer accessId,

            Boolean status

    ) {

        RoleAccess entity =

                repository

                        .findByRoleRoleIdAndAccessId(

                                roleId,

                                accessId

                        )

                        .orElseThrow(

                                () ->

                                        new RuntimeException(

                                                "Role access not found"

                                        )

                        );

        entity.setStatus(
                status
        );

        entity.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(
                entity
        );

        return status

                ?

                "Access granted successfully"

                :

                "Access revoked successfully";

    }

}

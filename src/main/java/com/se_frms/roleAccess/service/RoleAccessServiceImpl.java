package com.se_frms.roleAccess.service;

import com.se_frms.access.model.AccessMaster;
import com.se_frms.access.repository.AccessMasterRepository;
import com.se_frms.common.service.CreatedByResolver;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.se_frms.common.security.CurrentUserService;
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
    private final CurrentUserService currentUserService;
    private final CreatedByResolver createdByResolver;

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
                            .createdBy(
                                    currentUserService.getCurrentUserId()
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

        Map<String, String> workingFilters =
                new HashMap<>(
                        filters == null
                                ? Map.of()
                                : filters
                );

        String search =
                workingFilters.remove(
                        "search"
                );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        workingFilters,
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
                                DynamicFilterSpecification
                                        .<RoleAccess>build(
                                        workingFilters,
                                        FILTER_FIELDS
                                )
                        )
                        .and(
                                buildSearchSpecification(
                                        search
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

                                        .id(
                                                v.getId()
                                        )

                                        .roleId(
                                                v.getRole().getRoleId()
                                        )

                                        .roleName(
                                                v.getRole().getRoleName()
                                        )

                                        .accessId(
                                                v.getAccess().getId()
                                        )

                                        .accessNames(
                                                List.of(
                                                        v.getAccess().getAccessName()
                                                )
                                        )

                                        .status(
                                                v.getStatus()
                                        )

                                        .createdBy(
                                                createdByResolver.resolve(v.getCreatedBy())
                                        )

                                        .createdDate(
                                                v.getCreatedDate()
                                        )

                                        .updatedAt(
                                                v.getUpdatedAt()
                                        )

                                        .build()

                );

    }

    private Specification<RoleAccess> buildSearchSpecification(
            String search
    ) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String keyword =
                    "%"
                            + search
                            .trim()
                            .toLowerCase(Locale.ROOT)
                            + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("role").get("roleName")
                            ),
                            keyword
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("access").get("accessName")
                            ),
                            keyword
                    )
            );
        };
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

                .id(
                        mappings.get(0).getId()
                )

                .roleId(
                        roleId
                )

                .roleName(

                        mappings

                                .get(0)

                                .getRole()

                                .getRoleName()

                )

                .accessId(
                        mappings.get(0).getAccess().getId()
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

                .status(
                        mappings.get(0).getStatus()
                )

                .createdBy(
                        createdByResolver.resolve(mappings.get(0).getCreatedBy())
                )

                .createdDate(
                        mappings.get(0).getCreatedDate()
                )

                .updatedAt(
                        mappings.get(0).getUpdatedAt()
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

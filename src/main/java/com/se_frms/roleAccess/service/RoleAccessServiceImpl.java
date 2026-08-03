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
import com.se_frms.roleAccess.dto.RoleAccessUpdateRequestDTO;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import com.se_frms.auth.exception.InvalidRequestException;
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
    public List<RoleAccessResponseDTO> create(RoleAccessRequestDTO request) {

        RoleMaster role =
                roleRepository
                        .findById(request.getRoleId())
                        .orElseThrow(
                                () -> new InvalidRequestException("Role not found")
                        );

        List<Integer> requestedAccessIds =
                request.getAccessIds()
                        .stream()
                        .distinct()
                        .toList();

        List<RoleAccess> existingMappings =
                repository.findByRoleRoleId(role.getRoleId());

        List<Integer> alreadyExistingAccessIds =
                requestedAccessIds
                        .stream()
                        .filter(accessId ->
                                existingMappings
                                        .stream()
                                        .anyMatch(mapping ->
                                                mapping.getAccess().getId().equals(accessId)
                                        )
                        )
                        .toList();

        if (!alreadyExistingAccessIds.isEmpty()) {
            throw new InvalidRequestException(
                    "Role access already exists for access ids: "
                            + alreadyExistingAccessIds
            );
        }

        Integer loggedInAdminId =
                currentUserService.getCurrentUserId();

        LocalDateTime now =
                LocalDateTime.now();

        for (Integer accessId : requestedAccessIds) {

            AccessMaster access =
                    accessRepository
                            .findById(accessId)
                            .orElseThrow(
                                    () -> new InvalidRequestException("Access not found")
                            );

            repository.save(
                    RoleAccess
                            .builder()
                            .role(role)
                            .access(access)
                            .status(true)
                            .createdBy(loggedInAdminId)
                            .createdDate(now)
                            .updatedAt(now)
                            .build()
            );
        }

        return getByRole(role.getRoleId());
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
                        "createdDate",
                        Sort.Direction.DESC
                );

        Specification<RoleAccess> specification =
                DynamicFilterSpecification
                        .<RoleAccess>build(
                                workingFilters,
                                FILTER_FIELDS
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

                .map(this::mapToResponse);

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
    public List<RoleAccessResponseDTO> getByRole(Integer roleId) {

        List<RoleAccess> mappings =
                repository.findByRoleRoleIdOrderByAccessAccessNameAsc(roleId);

        if (mappings.isEmpty()) {
            throw new RuntimeException("No access found");
        }

        return mappings
                .stream()
                .map(this::mapToResponse)
                .toList();
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
        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy(
                    currentUserService.getCurrentUserId()
            );
        }

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

    @Override
    public List<RoleAccessResponseDTO> updateRoleAccess(
            Integer roleId,
            RoleAccessUpdateRequestDTO request
    ) {

        RoleMaster role =
                roleRepository
                        .findById(roleId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Role not found"
                                )
                        );

        Set<Integer> selectedAccessIds =
                Set.copyOf(
                        request.getAccessIds()
                );

        Map<Integer, AccessMaster> selectedAccessMap =
                new HashMap<>();

        for (Integer accessId : selectedAccessIds) {

            AccessMaster access =
                    accessRepository
                            .findById(accessId)
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Access not found"
                                    )
                            );

            selectedAccessMap.put(
                    accessId,
                    access
            );
        }

        List<RoleAccess> existingMappings =
                repository.findByRoleRoleId(roleId);

        Map<Integer, RoleAccess> existingMappingByAccessId =
                new HashMap<>();

        Integer loggedInAdminId =
                currentUserService.getCurrentUserId();

        LocalDateTime now =
                LocalDateTime.now();

        for (RoleAccess mapping : existingMappings) {

            existingMappingByAccessId.put(
                    mapping.getAccess().getId(),
                    mapping
            );

            Boolean shouldBeActive =
                    selectedAccessIds.contains(
                            mapping.getAccess().getId()
                    );

            mapping.setStatus(shouldBeActive);

            if (mapping.getCreatedBy() == null) {
                mapping.setCreatedBy(loggedInAdminId);
            }

            mapping.setUpdatedAt(now);

            repository.save(mapping);
        }

        for (Map.Entry<Integer, AccessMaster> entry : selectedAccessMap.entrySet()) {

            Integer accessId =
                    entry.getKey();

            if (existingMappingByAccessId.containsKey(accessId)) {
                continue;
            }

            RoleAccess newMapping =
                    RoleAccess
                            .builder()
                            .role(role)
                            .access(entry.getValue())
                            .status(true)
                            .createdBy(loggedInAdminId)
                            .createdDate(now)
                            .updatedAt(now)
                            .build();

            repository.save(newMapping);
        }

        return getByRole(roleId);
    }
    private RoleAccessResponseDTO mapToResponse(RoleAccess v) {
        return RoleAccessResponseDTO
                .builder()
                .id(v.getId())
                .roleId(v.getRole().getRoleId())
                .roleName(v.getRole().getRoleName())
                .accessId(v.getAccess().getId())
                .accessName(v.getAccess().getAccessName())
                .status(v.getStatus())
                .createdBy(createdByResolver.resolve(v.getCreatedBy()))
                .createdDate(v.getCreatedDate())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    @Override
    public RoleAccessResponseDTO updateStatusById(
            Integer id,
            Boolean status
    ) {

        RoleAccess entity =
                repository
                        .findById(id)
                        .orElseThrow(
                                () -> new InvalidRequestException("Role access not found")
                        );

        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());

        RoleAccess saved =
                repository.save(entity);

        return mapToResponse(saved);
    }
}

package com.se_frms.access.service;

import com.se_frms.access.dto.AccessRequestDTO;
import com.se_frms.access.dto.AccessResponseDTO;
import com.se_frms.access.model.AccessMaster;
import com.se_frms.access.repository.AccessMasterRepository;
import com.se_frms.common.util.DynamicFilterSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.se_frms.common.security.CurrentUserService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessServiceImpl
        implements AccessService {

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("accessName", "accessName"),
                    Map.entry("status", "status"),
                    Map.entry("createdBy", "createdBy"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final AccessMasterRepository repository;
    private final CurrentUserService currentUserService;

    @Override
    public AccessResponseDTO create(
            AccessRequestDTO request
    ) {

        log.info("Create access service started, accessName={}", request.getAccessName());

        repository
                .findByAccessName(
                        request.getAccessName()
                )

                .ifPresent(v -> {

                    log.warn("Create access failed because access already exists, accessName={}", request.getAccessName());

                    throw new RuntimeException(
                            "Access already exists"
                    );

                });

        AccessMaster entity =
                AccessMaster
                        .builder()

                        .accessName(
                                request.getAccessName()
                        )

                        .status(
                                true
                        )
                        .createdBy(
                                currentUserService.getCurrentUserId()
                        )

                        .build();

        AccessResponseDTO response =
                map(

                        repository.save(
                                entity
                        )

                );

        log.info("Access created successfully, accessName={}", request.getAccessName());

        return response;

    }

    @Override
    public Page<AccessResponseDTO> getAll(
            int page,
            int size,
            Map<String, String> filters
    ) {

        log.info(
                "Fetch all access service started, page={}, size={}",
                page,
                size
        );

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
                        "accessName",
                        Sort.Direction.ASC
                );

        Specification<AccessMaster> specification =
                DynamicFilterSpecification
                        .<AccessMaster>build(
                                workingFilters,
                                FILTER_FIELDS
                        )
                        .and(
                                buildSearchSpecification(
                                        search
                                )
                        );

        Page<AccessResponseDTO> response =
                repository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(this::map);

        log.info(
                "Access list fetched successfully, count={}",
                response.getNumberOfElements()
        );

        return response;

    }

    private Specification<AccessMaster> buildSearchSpecification(
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

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("accessName")
                    ),
                    keyword
            );
        };
    }

    @Override
    public AccessResponseDTO getById(
            Integer id
    ) {

        log.info("Fetch access by id service started, id={}", id);

        AccessResponseDTO response =
                map(

                        repository
                                .findById(id)

                                .orElseThrow(

                                        () -> {
                                            log.warn("Fetch access failed because access was not found, id={}", id);

                                            return new RuntimeException(
                                                    "Access not found"
                                            );
                                        }
                                )

                );

        log.info("Access fetched successfully, id={}", id);

        return response;

    }

    @Override
    public AccessResponseDTO update(
            Integer id,
            AccessRequestDTO request
    ) {

        log.info("Update access service started, id={}", id);

        AccessMaster entity =
                repository
                        .findById(id)

                        .orElseThrow(

                                () -> {
                                    log.warn("Update access failed because access was not found, id={}", id);

                                    return new RuntimeException(
                                            "Access not found"
                                    );
                                }

                        );

        repository
                .findByAccessName(
                        request.getAccessName()
                )

                .ifPresent(access -> {

                    if (!access.getId().equals(id)) {

                        log.warn("Update access failed because access already exists, id={}, accessName={}", id, request.getAccessName());

                        throw new RuntimeException(
                                "Access already exists"
                        );

                    }

                });

        entity.setAccessName(
                request.getAccessName()
        );

        entity.setStatus(
                request.getStatus()
        );

        entity.setUpdatedAt(
                LocalDateTime.now()
        );

        AccessResponseDTO response =
                map(

                        repository.save(
                                entity
                        )

                );

        log.info("Access updated successfully, id={}", id);

        return response;

    }

    @Override
    public String updateStatus(
            Integer id,
            Boolean status
    ) {

        log.info("Update access status service started, id={}, status={}", id, status);

        AccessMaster entity =
                repository
                        .findById(id)

                        .orElseThrow(

                                () -> {
                                    log.warn("Update access status failed because access was not found, id={}", id);

                                    return new RuntimeException(
                                            "Access not found"
                                    );
                                }

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

        log.info("Access status updated successfully, id={}, status={}", id, status);

        return status

                ?

                "Access enabled successfully"

                :

                "Access disabled successfully";

    }

    @Override
    public String delete(
            Integer id
    ) {

        log.info("Delete access service started, id={}", id);

        AccessMaster entity =
                repository
                        .findById(id)

                        .orElseThrow(

                                () -> {
                                    log.warn("Delete access failed because access was not found, id={}", id);

                                    return new RuntimeException(
                                            "Access not found"
                                    );
                                }

                        );

        entity.setStatus(
                false
        );

        entity.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(
                entity
        );

        log.info("Access deleted successfully, id={}", id);

        return "Access deleted successfully";

    }

    private AccessResponseDTO map(
            AccessMaster entity
    ) {

        return AccessResponseDTO
                .builder()

                .id(
                        entity.getId()
                )

                .accessName(
                        entity.getAccessName()
                )

                .status(
                        entity.getStatus()
                )
                .createdBy(
                        entity.getCreatedBy()
                )

                .createdDate(
                        entity.getCreatedDate()
                )

                .updatedAt(
                        entity.getUpdatedAt()
                )

                .build();

    }

}

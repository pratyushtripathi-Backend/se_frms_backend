package com.se_frms.ruleCategory.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.common.security.AccessPermissionService;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.common.security.XssUtil;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.util.PaginationUtil;
import com.se_frms.ruleCategory.dto.*;
import com.se_frms.ruleCategory.model.RuleCategory;
import com.se_frms.ruleCategory.repository.RuleCategoryRepository;
import com.se_frms.common.util.DynamicFilterSpecification;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleCategoryServiceImpl
        implements RuleCategoryService {

    private static final String RULE_CATEGORY_VIEW = "RULE_CATEGORY_VIEW";

    private static final String RULE_CATEGORY_CREATE = "RULE_CATEGORY_CREATE";

    private static final String RULE_CATEGORY_UPDATE = "RULE_CATEGORY_UPDATE";

    private static final String RULE_CATEGORY_DELETE = "RULE_CATEGORY_DELETE";

    private final RuleCategoryRepository ruleCategoryRepository;

    private final CurrentUserService currentUserService;

    private final AccessPermissionService accessPermissionService;

    private final CreatedByResolver createdByResolver;
    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("categoryName", "categoryName"),
                    Map.entry("status", "status"),
                    Map.entry("createdBy", "createdBy"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    @Override
    public RuleCategoryResponseDTO createCategory(
            RuleCategoryRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                RULE_CATEGORY_CREATE
        );

        String categoryName =
                cleanText(request.getCategoryName());

        log.info("Create rule category started, categoryName={}", categoryName);

        if (ruleCategoryRepository.existsByCategoryNameIgnoreCase(categoryName)) {
            throw new InvalidRequestException("Category already exists");
        }

        Integer loggedInUserId =
                currentUserService.getCurrentUserId();

        RuleCategory category =
                RuleCategory.builder()
                        .categoryName(categoryName)
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : true
                        )
                        .createdBy(loggedInUserId)
                        .build();

        RuleCategory savedCategory =
                ruleCategoryRepository.save(category);

        log.info("Rule category created successfully, id={}", savedCategory.getId());

        return mapToResponse(savedCategory);
    }

    @Override
    public RuleCategoryResponseDTO updateCategory(
            Integer id,
            RuleCategoryRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                RULE_CATEGORY_UPDATE
        );

        log.info("Update rule category started, id={}", id);

        RuleCategory category =
                getCategoryEntity(id);

        String categoryName =
                cleanText(request.getCategoryName());

        ruleCategoryRepository
                .findByCategoryNameIgnoreCase(categoryName)
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(id)) {
                        throw new InvalidRequestException(
                                "Category already exists"
                        );
                    }
                });

        category.setCategoryName(categoryName);

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        category.setUpdatedAt(LocalDateTime.now());

        RuleCategory updatedCategory =
                ruleCategoryRepository.save(category);

        log.info("Rule category updated successfully, id={}", id);

        return mapToResponse(updatedCategory);
    }

    @Override
    public RuleCategoryResponseDTO updateStatus(
            Integer id,
            RuleCategoryStatusRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                RULE_CATEGORY_UPDATE
        );

        log.info("Update rule category status started, id={}, status={}",
                id,
                request.getStatus()
        );

        RuleCategory category =
                getCategoryEntity(id);

        category.setStatus(request.getStatus());
        category.setUpdatedAt(LocalDateTime.now());

        RuleCategory updatedCategory =
                ruleCategoryRepository.save(category);

        log.info("Rule category status updated successfully, id={}", id);

        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                RULE_CATEGORY_DELETE
        );

        log.info("Delete rule category started, id={}", id);

        RuleCategory category =
                getCategoryEntity(id);

        category.setStatus(false);
        category.setUpdatedAt(LocalDateTime.now());

        ruleCategoryRepository.save(category);

        log.info("Rule category deleted successfully, id={}", id);
    }

    @Override
    public Page<RuleCategoryResponseDTO> getAllCategories(
            Integer page,
            Integer size,
            Map<String, String> filters
    ) {

        accessPermissionService.validateAccess(
                RULE_CATEGORY_VIEW
        );

        int pageNumber =
                page == null
                        ? 0
                        : page;

        int pageSize =
                size == null
                        ? 10
                        : size;

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
                        pageNumber,
                        pageSize,
                        workingFilters,
                        FILTER_FIELDS,
                        "createdDate",
                        Sort.Direction.DESC
                );

        Specification<RuleCategory> specification =
                DynamicFilterSpecification.build(
                        workingFilters,
                        FILTER_FIELDS
                );

        Specification<RuleCategory> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

        return ruleCategoryRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public RuleCategoryResponseDTO getCategoryById(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                RULE_CATEGORY_VIEW
        );

        RuleCategory category =
                getCategoryEntity(id);

        return mapToResponse(category);
    }

    private RuleCategory getCategoryEntity(
            Integer id
    ) {

        return ruleCategoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new InvalidRequestException(
                                "Category not found"
                        )
                );
    }

    private RuleCategoryResponseDTO mapToResponse(
            RuleCategory category
    ) {

        return RuleCategoryResponseDTO
                .builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .status(category.getStatus())
                .createdBy(createdByResolver.resolve(category.getCreatedBy()))
                .createdDate(category.getCreatedDate())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private Specification<RuleCategory> buildSearchSpecification(
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
                criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("categoryName")
                        ),
                        keyword
                );
    }

    private String cleanText(
            String value
    ) {

        if (value == null) {
            return null;
        }

        return XssUtil.clean(value).trim();
    }
}

package com.se_frms.decisionPolicy.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.common.security.AccessPermissionService;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.common.security.XssUtil;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.decisionPolicy.dto.*;
import com.se_frms.decisionPolicy.model.DecisionPolicy;
import com.se_frms.decisionPolicy.repository.DecisionPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionPolicyServiceImpl
        implements DecisionPolicyService {

    private static final String DECISION_POLICY_VIEW = "DECISION_POLICY_VIEW";

    private static final String DECISION_POLICY_CREATE = "DECISION_POLICY_CREATE";

    private static final String DECISION_POLICY_UPDATE = "DECISION_POLICY_UPDATE";

    private static final String DECISION_POLICY_DELETE = "DECISION_POLICY_DELETE";

    private final DecisionPolicyRepository decisionPolicyRepository;

    private final CurrentUserService currentUserService;

    private final AccessPermissionService accessPermissionService;

    private final CreatedByResolver createdByResolver;

    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("description", "description"),
                    Map.entry("allowMinScore", "allowMinScore"),
                    Map.entry("allowMaxScore", "allowMaxScore"),
                    Map.entry("reviewMinScore", "reviewMinScore"),
                    Map.entry("reviewMaxScore", "reviewMaxScore"),
                    Map.entry("blockMinScore", "blockMinScore"),
                    Map.entry("blockMaxScore", "blockMaxScore"),
                    Map.entry("status", "status"),
                    Map.entry("createdBy", "createdBy"),
                    Map.entry("createdAt", "createdAt"),
                    Map.entry("updatedAt", "updatedAt")
            );

    @Override
    public DecisionPolicyResponseDTO createDecisionPolicy(
            DecisionPolicyRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                DECISION_POLICY_CREATE
        );

        validateScoreRanges(request);

        String description =
                cleanText(request.getDescription());

        log.info("Create decision policy started, description={}", description);

        DecisionPolicy decisionPolicy =
                DecisionPolicy.builder()
                        .description(description)
                        .allowMinScore(request.getAllowMinScore())
                        .allowMaxScore(request.getAllowMaxScore())
                        .reviewMinScore(request.getReviewMinScore())
                        .reviewMaxScore(request.getReviewMaxScore())
                        .blockMinScore(request.getBlockMinScore())
                        .blockMaxScore(request.getBlockMaxScore())
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : true
                        )
                        .createdBy(currentUserService.getCurrentUserId())
                        .build();

        DecisionPolicy savedDecisionPolicy =
                decisionPolicyRepository.save(decisionPolicy);

        log.info("Decision policy created successfully, id={}", savedDecisionPolicy.getId());

        return mapToResponse(savedDecisionPolicy);
    }

    @Override
    public DecisionPolicyResponseDTO updateDecisionPolicy(
            Integer id,
            DecisionPolicyRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                DECISION_POLICY_UPDATE
        );

        validateScoreRanges(request);

        log.info("Update decision policy started, id={}", id);

        DecisionPolicy decisionPolicy =
                getDecisionPolicyEntity(id);

        decisionPolicy.setDescription(
                cleanText(request.getDescription())
        );
        decisionPolicy.setAllowMinScore(request.getAllowMinScore());
        decisionPolicy.setAllowMaxScore(request.getAllowMaxScore());
        decisionPolicy.setReviewMinScore(request.getReviewMinScore());
        decisionPolicy.setReviewMaxScore(request.getReviewMaxScore());
        decisionPolicy.setBlockMinScore(request.getBlockMinScore());
        decisionPolicy.setBlockMaxScore(request.getBlockMaxScore());

        if (request.getStatus() != null) {
            decisionPolicy.setStatus(request.getStatus());
        }

        decisionPolicy.setUpdatedAt(LocalDateTime.now());

        DecisionPolicy updatedDecisionPolicy =
                decisionPolicyRepository.save(decisionPolicy);

        log.info("Decision policy updated successfully, id={}", id);

        return mapToResponse(updatedDecisionPolicy);
    }

    @Override
    public DecisionPolicyResponseDTO updateStatus(
            Integer id,
            DecisionPolicyStatusRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                DECISION_POLICY_UPDATE
        );

        log.info(
                "Update decision policy status started, id={}, status={}",
                id,
                request.getStatus()
        );

        DecisionPolicy decisionPolicy =
                getDecisionPolicyEntity(id);

        decisionPolicy.setStatus(request.getStatus());
        decisionPolicy.setUpdatedAt(LocalDateTime.now());

        DecisionPolicy updatedDecisionPolicy =
                decisionPolicyRepository.save(decisionPolicy);

        log.info("Decision policy status updated successfully, id={}", id);

        return mapToResponse(updatedDecisionPolicy);
    }

    @Override
    public void deleteDecisionPolicy(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                DECISION_POLICY_DELETE
        );

        log.info("Delete decision policy started, id={}", id);

        DecisionPolicy decisionPolicy =
                getDecisionPolicyEntity(id);

        decisionPolicy.setStatus(false);
        decisionPolicy.setUpdatedAt(LocalDateTime.now());

        decisionPolicyRepository.save(decisionPolicy);

        log.info("Decision policy deleted successfully, id={}", id);
    }

    @Override
    public Page<DecisionPolicyResponseDTO> getAllDecisionPolicies(
            Integer page,
            Integer size,
            Map<String, String> filters
    ) {

        accessPermissionService.validateAccess(
                DECISION_POLICY_VIEW
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
                        "createdAt",
                        Sort.Direction.DESC
                );

        Specification<DecisionPolicy> specification =
                DynamicFilterSpecification.build(
                        workingFilters,
                        FILTER_FIELDS
                );

        Specification<DecisionPolicy> searchSpecification =
                buildSearchSpecification(search);

        if (searchSpecification != null) {
            specification =
                    specification.and(searchSpecification);
        }

        return decisionPolicyRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public DecisionPolicyResponseDTO getDecisionPolicyById(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                DECISION_POLICY_VIEW
        );

        DecisionPolicy decisionPolicy =
                getDecisionPolicyEntity(id);

        return mapToResponse(decisionPolicy);
    }

    private DecisionPolicy getDecisionPolicyEntity(
            Integer id
    ) {

        return decisionPolicyRepository
                .findById(id)
                .orElseThrow(
                        () -> new InvalidRequestException(
                                "Decision policy not found"
                        )
                );
    }

    private void validateScoreRanges(
            DecisionPolicyRequestDTO request
    ) {

        validateMinMax(
                request.getAllowMinScore(),
                request.getAllowMaxScore(),
                "Allow"
        );
        validateMinMax(
                request.getReviewMinScore(),
                request.getReviewMaxScore(),
                "Review"
        );
        validateMinMax(
                request.getBlockMinScore(),
                request.getBlockMaxScore(),
                "Block"
        );

        if (request.getReviewMinScore() <= request.getAllowMaxScore()) {
            throw new InvalidRequestException(
                    "Review minimum score must be greater than allow maximum score"
            );
        }

        if (request.getBlockMinScore() <= request.getReviewMaxScore()) {
            throw new InvalidRequestException(
                    "Block minimum score must be greater than review maximum score"
            );
        }
    }

    private void validateMinMax(
            Integer minScore,
            Integer maxScore,
            String decisionType
    ) {

        if (minScore > maxScore) {
            throw new InvalidRequestException(
                    decisionType + " minimum score must be less than or equal to maximum score"
            );
        }
    }

    private DecisionPolicyResponseDTO mapToResponse(
            DecisionPolicy decisionPolicy
    ) {

        return DecisionPolicyResponseDTO
                .builder()
                .id(decisionPolicy.getId())
                .description(decisionPolicy.getDescription())
                .allowMinScore(decisionPolicy.getAllowMinScore())
                .allowMaxScore(decisionPolicy.getAllowMaxScore())
                .reviewMinScore(decisionPolicy.getReviewMinScore())
                .reviewMaxScore(decisionPolicy.getReviewMaxScore())
                .blockMinScore(decisionPolicy.getBlockMinScore())
                .blockMaxScore(decisionPolicy.getBlockMaxScore())
                .status(decisionPolicy.getStatus())
                .createdBy(createdByResolver.resolve(decisionPolicy.getCreatedBy()))
                .createdAt(decisionPolicy.getCreatedAt())
                .updatedAt(decisionPolicy.getUpdatedAt())
                .build();
    }

    private Specification<DecisionPolicy> buildSearchSpecification(
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
                                root.get("description")
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

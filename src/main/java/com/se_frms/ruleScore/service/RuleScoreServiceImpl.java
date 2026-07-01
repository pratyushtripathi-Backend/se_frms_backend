package com.se_frms.ruleScore.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.common.security.AccessPermissionService;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.fraudRule.model.FraudRule;
import com.se_frms.fraudRule.repository.FraudRuleRepository;
import com.se_frms.ruleScore.dto.*;
import com.se_frms.ruleScore.model.RuleScore;
import com.se_frms.ruleScore.repository.RuleScoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import com.se_frms.common.util.DynamicFilterSpecification;
import org.springframework.data.jpa.domain.Specification;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleScoreServiceImpl
        implements RuleScoreService {

    private static final String RULE_SCORE_VIEW = "RULE_SCORE_VIEW";

    private static final String RULE_SCORE_CREATE = "RULE_SCORE_CREATE";

    private static final String RULE_SCORE_UPDATE = "RULE_SCORE_UPDATE";

    private static final String RULE_SCORE_DELETE = "RULE_SCORE_DELETE";

    private final RuleScoreRepository ruleScoreRepository;

    private final FraudRuleRepository fraudRuleRepository;

    private final CurrentUserService currentUserService;

    private final AccessPermissionService accessPermissionService;
    private static final Map<String, String> FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("ruleId", "rule.id"),
                    Map.entry("ruleCode", "rule.ruleCode"),
                    Map.entry("ruleName", "rule.ruleName"),
                    Map.entry("score", "score"),
                    Map.entry("status", "status"),
                    Map.entry("createdBy", "createdBy"),
                    Map.entry("createdAt", "createdAt"),
                    Map.entry("updatedAt", "updatedAt")
            );

    @Override
    public RuleScoreResponseDTO createRuleScore(
            RuleScoreRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                RULE_SCORE_CREATE
        );

        log.info(
                "Create rule score started, ruleId={}",
                request.getRuleId()
        );

        FraudRule fraudRule =
                getActiveFraudRule(request.getRuleId());

        if (ruleScoreRepository.existsByRule(fraudRule)) {
            throw new InvalidRequestException(
                    "Rule score already exists"
            );
        }

        Integer loggedInUserId =
                currentUserService.getCurrentUserId();

        RuleScore ruleScore =
                RuleScore.builder()
                        .rule(fraudRule)
                        .score(request.getScore())
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : true
                        )
                        .createdBy(loggedInUserId)
                        .build();

        RuleScore savedRuleScore =
                ruleScoreRepository.save(ruleScore);

        log.info(
                "Rule score created successfully, id={}",
                savedRuleScore.getId()
        );

        return mapToResponse(savedRuleScore);
    }

    @Override
    public RuleScoreResponseDTO updateRuleScore(
            Integer id,
            RuleScoreRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                RULE_SCORE_UPDATE
        );

        log.info(
                "Update rule score started, id={}",
                id
        );

        RuleScore ruleScore =
                getRuleScoreEntity(id);

        FraudRule fraudRule =
                getActiveFraudRule(request.getRuleId());

        ruleScoreRepository
                .findByRule(fraudRule)
                .ifPresent(existingRuleScore -> {
                    if (!existingRuleScore.getId().equals(id)) {
                        throw new InvalidRequestException(
                                "Rule score already exists"
                        );
                    }
                });

        ruleScore.setRule(fraudRule);
        ruleScore.setScore(request.getScore());

        if (request.getStatus() != null) {
            ruleScore.setStatus(request.getStatus());
        }

        RuleScore updatedRuleScore =
                ruleScoreRepository.save(ruleScore);

        log.info(
                "Rule score updated successfully, id={}",
                id
        );

        return mapToResponse(updatedRuleScore);
    }

    @Override
    public RuleScoreResponseDTO updateStatus(
            Integer id,
            RuleScoreStatusRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                RULE_SCORE_UPDATE
        );

        log.info(
                "Update rule score status started, id={}, status={}",
                id,
                request.getStatus()
        );

        RuleScore ruleScore =
                getRuleScoreEntity(id);

        ruleScore.setStatus(request.getStatus());

        RuleScore updatedRuleScore =
                ruleScoreRepository.save(ruleScore);

        log.info(
                "Rule score status updated successfully, id={}",
                id
        );

        return mapToResponse(updatedRuleScore);
    }

    @Override
    public void deleteRuleScore(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                RULE_SCORE_DELETE
        );

        log.info(
                "Delete rule score started, id={}",
                id
        );

        RuleScore ruleScore =
                getRuleScoreEntity(id);

        ruleScore.setStatus(false);

        ruleScoreRepository.save(ruleScore);

        log.info(
                "Rule score deleted successfully, id={}",
                id
        );
    }

    @Override
    public Page<RuleScoreResponseDTO> getAllRuleScores(
            Integer page,
            Integer size,
            Map<String, String> filters
    ) {

        accessPermissionService.validateAccess(
                RULE_SCORE_VIEW
        );
        log.info("Rule score filters received: {}", filters);

        int pageNumber =
                page == null
                        ? 0
                        : page;

        int pageSize =
                size == null
                        ? 10
                        : size;

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        pageNumber,
                        pageSize,
                        filters,
                        FILTER_FIELDS,
                        "rule.ruleName",
                        Sort.Direction.ASC
                );

        Specification<RuleScore> specification =
                DynamicFilterSpecification.build(
                        filters,
                        FILTER_FIELDS
                );

        if (!filters.containsKey("status")) {
            specification =
                    DynamicFilterSpecification
                            .<RuleScore>equal(
                                    "status",
                                    true
                            )
                            .and(specification);
        }

        return ruleScoreRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public RuleScoreResponseDTO getRuleScoreById(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                RULE_SCORE_VIEW
        );

        RuleScore ruleScore =
                getRuleScoreEntity(id);

        return mapToResponse(ruleScore);
    }

    @Override
    public RuleScoreResponseDTO getRuleScoreByRuleId(
            Integer ruleId
    ) {

        accessPermissionService.validateAccess(
                RULE_SCORE_VIEW
        );

        FraudRule fraudRule =
                getActiveFraudRule(ruleId);

        RuleScore ruleScore =
                ruleScoreRepository
                        .findByRule(fraudRule)
                        .orElseThrow(
                                () -> new InvalidRequestException(
                                        "Rule score not found"
                                )
                        );

        return mapToResponse(ruleScore);
    }

    private RuleScore getRuleScoreEntity(
            Integer id
    ) {

        return ruleScoreRepository
                .findById(id)
                .orElseThrow(
                        () -> new InvalidRequestException(
                                "Rule score not found"
                        )
                );
    }

    private FraudRule getActiveFraudRule(
            Integer ruleId
    ) {

        FraudRule fraudRule =
                fraudRuleRepository
                        .findById(ruleId)
                        .orElseThrow(
                                () -> new InvalidRequestException(
                                        "Fraud rule not found"
                                )
                        );

        if (!Boolean.TRUE.equals(fraudRule.getStatus())) {
            throw new InvalidRequestException(
                    "Fraud rule is inactive"
            );
        }

        return fraudRule;
    }

    private RuleScoreResponseDTO mapToResponse(
            RuleScore ruleScore
    ) {

        FraudRule fraudRule =
                ruleScore.getRule();

        return RuleScoreResponseDTO
                .builder()
                .id(ruleScore.getId())
                .ruleId(
                        fraudRule != null
                                ? fraudRule.getId()
                                : null
                )
                .ruleCode(
                        fraudRule != null
                                ? fraudRule.getRuleCode()
                                : null
                )
                .ruleName(
                        fraudRule != null
                                ? fraudRule.getRuleName()
                                : null
                )
                .score(ruleScore.getScore())
                .status(ruleScore.getStatus())
                .createdBy(ruleScore.getCreatedBy())
                .createdAt(ruleScore.getCreatedAt())
                .updatedAt(ruleScore.getUpdatedAt())
                .build();
    }
}

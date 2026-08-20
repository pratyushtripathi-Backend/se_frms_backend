package com.se_frms.internal.service;

import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.decisionPolicy.model.DecisionPolicy;
import com.se_frms.decisionPolicy.repository.DecisionPolicyRepository;
import com.se_frms.fraudRule.model.FraudRule;
import com.se_frms.internal.dto.DecisionPolicyCacheResponseDTO;
import com.se_frms.internal.dto.RuleCacheSyncResponseDTO;
import com.se_frms.ruleCategory.model.RuleCategory;
import com.se_frms.ruleScore.model.RuleScore;
import com.se_frms.ruleScore.repository.RuleScoreRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalRuleCacheServiceImpl implements InternalRuleCacheService {

    private final RuleScoreRepository ruleScoreRepository;

    private final DecisionPolicyRepository decisionPolicyRepository;

    private final CreatedByResolver createdByResolver;

    @Override
    @Transactional(readOnly = true)
    public List<RuleCacheSyncResponseDTO> getActiveRulesForCache() {

        return ruleScoreRepository
                .findActiveRuleScoresForCache()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionPolicyCacheResponseDTO getActiveDecisionPolicyForCache() {

        DecisionPolicy decisionPolicy =
                decisionPolicyRepository
                        .findFirstByStatusTrueOrderByUpdatedAtDesc()
                        .orElse(null);

        if (decisionPolicy == null) {
            return null;
        }

        String createdBy =
                createdByResolver.resolve(decisionPolicy.getCreatedBy());

        if (createdBy == null) {
            createdBy = "SYSTEM";
        }

        return DecisionPolicyCacheResponseDTO
                .builder()
                .policyId(decisionPolicy.getId())
                .description(decisionPolicy.getDescription())
                .allowMinScore(decisionPolicy.getAllowMinScore())
                .allowMaxScore(decisionPolicy.getAllowMaxScore())
                .reviewMinScore(decisionPolicy.getReviewMinScore())
                .reviewMaxScore(decisionPolicy.getReviewMaxScore())
                .blockMinScore(decisionPolicy.getBlockMinScore())
                .blockMaxScore(decisionPolicy.getBlockMaxScore())
                .status(decisionPolicy.getStatus())
                .createdBy(createdBy)
                .createdAt(decisionPolicy.getCreatedAt())
                .updatedAt(decisionPolicy.getUpdatedAt())
                .build();
    }

    private RuleCacheSyncResponseDTO mapToResponse(
            RuleScore ruleScore
    ) {

        FraudRule fraudRule =
                ruleScore.getRule();

        RuleCategory category =
                fraudRule.getCategory();

        String createdBy =
                createdByResolver.resolve(ruleScore.getCreatedBy());

        if (createdBy == null) {
            createdBy =
                    createdByResolver.resolve(fraudRule.getCreatedBy());
        }

        if (createdBy == null) {
            createdBy = "SYSTEM";
        }

        return RuleCacheSyncResponseDTO
                .builder()
                .ruleId(fraudRule.getId())
                .categoryId(category.getId())
                .ruleCode(fraudRule.getRuleCode())
                .ruleName(fraudRule.getRuleName())
                .ruleDescription(
                        fraudRule.getRuleDescription() == null
                                ? ""
                                : fraudRule.getRuleDescription()
                )
                .ruleExpression(fraudRule.getRuleExpression())
                .categoryName(category.getCategoryName())
                .ruleScore(ruleScore.getScore())
                .status(true)
                .createdBy(createdBy)
                .build();
    }
}

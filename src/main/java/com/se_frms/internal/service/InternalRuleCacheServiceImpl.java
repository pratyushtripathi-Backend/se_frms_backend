package com.se_frms.internal.service;

import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.fraudRule.model.FraudRule;
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
                .categoryName(category.getCategoryName())
                .ruleScore(ruleScore.getScore())
                .status(true)
                .createdBy(createdBy)
                .build();
    }
}
package com.se_frms.ruleScore.service;

import com.se_frms.ruleScore.dto.*;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface RuleScoreService {

    RuleScoreResponseDTO createRuleScore(
            RuleScoreRequestDTO request
    );

    RuleScoreResponseDTO updateRuleScore(
            Integer id,
            RuleScoreRequestDTO request
    );

    RuleScoreResponseDTO updateStatus(
            Integer id,
            RuleScoreStatusRequestDTO request
    );

    void deleteRuleScore(
            Integer id
    );

    Page<RuleScoreResponseDTO> getAllRuleScores(
            Integer page,
            Integer size,
            Map<String, String> filters
    );

    RuleScoreResponseDTO getRuleScoreById(
            Integer id
    );

    RuleScoreResponseDTO getRuleScoreByRuleId(
            Integer ruleId
    );
}

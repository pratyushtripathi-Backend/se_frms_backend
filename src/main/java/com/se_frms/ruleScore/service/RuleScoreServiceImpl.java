package com.se_frms.ruleScore.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.common.util.PaginationUtil;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleScoreServiceImpl
        implements RuleScoreService {

    private final RuleScoreRepository ruleScoreRepository;

    private final FraudRuleRepository fraudRuleRepository;

    private final CurrentUserService currentUserService;

    @Override
    public RuleScoreResponseDTO createRuleScore(
            RuleScoreRequestDTO request
    ) {

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
            Integer size
    ) {

        Pageable pageable =
                PaginationUtil.createPageable(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"
                        )
                );

        return ruleScoreRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public RuleScoreResponseDTO getRuleScoreById(
            Integer id
    ) {

        RuleScore ruleScore =
                getRuleScoreEntity(id);

        return mapToResponse(ruleScore);
    }

    @Override
    public RuleScoreResponseDTO getRuleScoreByRuleId(
            Integer ruleId
    ) {

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
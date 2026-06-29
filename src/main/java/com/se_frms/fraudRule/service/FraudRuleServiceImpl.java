package com.se_frms.fraudRule.service;

import com.se_frms.auth.exception.InvalidRequestException;
//import com.se_frms.common.exception.InvalidRequestException;

import com.se_frms.common.security.AccessPermissionService;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.fraudRule.dto.FraudRuleRequestDTO;
import com.se_frms.fraudRule.dto.FraudRuleResponseDTO;
import com.se_frms.fraudRule.dto.FraudRuleStatusDTO;
import com.se_frms.fraudRule.dto.FraudRuleUpdateDTO;

import com.se_frms.fraudRule.model.FraudRule;

import com.se_frms.fraudRule.repository.FraudRuleRepository;

import com.se_frms.ruleCategory.model.RuleCategory;
import com.se_frms.ruleCategory.repository.RuleCategoryRepository;

import lombok.RequiredArgsConstructor;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FraudRuleServiceImpl
        implements FraudRuleService {

    private static final String FRAUD_RULE_VIEW = "FRAUD_RULE_VIEW";

    private static final String FRAUD_RULE_CREATE = "FRAUD_RULE_CREATE";

    private static final String FRAUD_RULE_UPDATE = "FRAUD_RULE_UPDATE";

    private static final String FRAUD_RULE_DELETE = "FRAUD_RULE_DELETE";

    private final FraudRuleRepository repository;

    private final RuleCategoryRepository categoryRepository;

    private final CurrentUserService currentUserService;

    private final AccessPermissionService accessPermissionService;

    @Override
    public FraudRuleResponseDTO create(
            FraudRuleRequestDTO request
    ) {

        accessPermissionService.validateAccess(
                FRAUD_RULE_CREATE
        );

        Integer userId =
                currentUserService.getCurrentUserId();

        validateFraudRule(
                request.getRuleCode(),
                request.getRuleName(),
                null
        );

        RuleCategory category =
                getCategory(
                        request.getCategoryId()
                );

        FraudRule entity =

                FraudRule.builder()

                        .category(category)

                        .ruleCode(
                                request.getRuleCode()
                        )

                        .ruleName(
                                request.getRuleName()
                        )

                        .ruleDescription(
                                request.getRuleDescription()
                        )

                        .status(true)

                        .createdBy(userId)

                        .build();

        repository.save(entity);

        return map(entity);


    }

    @Override
    public FraudRuleResponseDTO update(


Integer id,

FraudRuleUpdateDTO request


    ) {

        accessPermissionService.validateAccess(
                FRAUD_RULE_UPDATE
        );

        FraudRule entity =

                repository

                        .findById(id)

                        .orElseThrow(

                                () ->
                                        new InvalidRequestException(
                                                "Fraud Rule not found"
                                        )

                        );

        validateFraudRule(

                request.getRuleCode(),

                request.getRuleName(),

                id

        );

        RuleCategory category =
                getCategory(
                        request.getCategoryId()
                );

        entity.setCategory(
                category
        );

        entity.setRuleCode(
                request.getRuleCode()
        );

        entity.setRuleName(
                request.getRuleName()
        );

        entity.setRuleDescription(
                request.getRuleDescription()
        );

        entity.setStatus(
                request.getStatus()
        );

        repository.save(
                entity
        );

        return map(
                entity
        );


    }

    private RuleCategory getCategory(
            Integer categoryId
    ) {


        return categoryRepository

                .findById(
                        categoryId
                )

                .orElseThrow(

                        () ->
                                new InvalidRequestException(
                                        "Category not found"
                                )

                );


    }

    private void validateFraudRule(

            String ruleCode,

            String ruleName,

            Integer excludeId

    ) {

        Optional<FraudRule> existingRule =

                repository.findByRuleCodeOrRuleNameIgnoreCase(

                        ruleCode,

                        ruleName

                );

        if (

                existingRule.isPresent()

                        &&

                        (
                                excludeId == null

                                        ||

                                        !existingRule
                                                .get()
                                                .getId()
                                                .equals(excludeId)
                        )

        ) {

            throw new InvalidRequestException(
                    "Fraud Rule with same code or name already exists"
            );

        }

    }


    @Override
    public void delete(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                FRAUD_RULE_DELETE
        );

        FraudRule entity =

                repository

                        .findById(
                                id
                        )

                        .orElseThrow(

                                () ->
                                        new InvalidRequestException(
                                                "Fraud rule not found"
                                        )

                        );

        repository.delete(
                entity
        );

    }

    @Override
    public FraudRuleResponseDTO getById(
            Integer id
    ) {

        accessPermissionService.validateAccess(
                FRAUD_RULE_VIEW
        );

        FraudRule entity =

                repository

                        .findById(
                                id
                        )

                        .orElseThrow(

                                () ->
                                        new InvalidRequestException(
                                                "Fraud rule not found"
                                        )

                        );

        return map(
                entity
        );

    }

    @Override
    public Page<FraudRuleResponseDTO> getAll(
            Pageable pageable
    ) {

        accessPermissionService.validateAccess(
                FRAUD_RULE_VIEW
        );

        return repository

                .findAll(
                        pageable
                )

                .map(
                        this::map
                );

    }

    @Override
    public List<FraudRuleResponseDTO> getByCategoryId(
            Integer categoryId
    ) {

        accessPermissionService.validateAccess(
                FRAUD_RULE_VIEW
        );

        return repository

                .findByCategoryId(
                        categoryId
                )

                .stream()

                .map(
                        this::map
                )

                .toList();

    }

    @Override
    public FraudRuleResponseDTO updateStatus(

            Integer id,

            FraudRuleStatusDTO request

    ) {

        accessPermissionService.validateAccess(
                FRAUD_RULE_UPDATE
        );

        FraudRule rule =

                repository

                        .findById(
                                id
                        )

                        .orElseThrow(

                                () ->

                                        new InvalidRequestException(
                                                "Fraud rule not found"
                                        )

                        );

        rule.setStatus(
                request.getStatus()
        );

        rule.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(
                rule
        );

        return map(
                rule
        );

    }




    private FraudRuleResponseDTO map(
            FraudRule entity
    ) {

        return FraudRuleResponseDTO

                .builder()

                .id(
                        entity.getId()
                )

                .categoryId(
                        entity.getCategory().getId()
                )

                .categoryName(
                        entity.getCategory()
                                .getCategoryName()
                )

                .ruleCode(
                        entity.getRuleCode()
                )

                .ruleName(
                        entity.getRuleName()
                )

                .ruleDescription(
                        entity.getRuleDescription()
                )

                .status(
                        entity.getStatus()
                )

                .createdBy(
                        entity.getCreatedBy()
                )

                .createdAt(
                        entity.getCreatedAt()
                )

                .updatedAt(
                        entity.getUpdatedAt()
                )

                .build();

    }

}

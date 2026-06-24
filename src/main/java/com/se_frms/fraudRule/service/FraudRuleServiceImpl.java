package com.se_frms.fraudRule.service;

import com.se_frms.auth.exception.InvalidRequestException;
//import com.se_frms.common.exception.InvalidRequestException;

import com.se_frms.fraudRule.dto.FraudRuleRequestDTO;
import com.se_frms.fraudRule.dto.FraudRuleResponseDTO;
import com.se_frms.fraudRule.dto.FraudRuleUpdateDTO;

import com.se_frms.fraudRule.model.FraudRule;

import com.se_frms.fraudRule.repository.FraudRuleRepository;

import com.se_frms.ruleCategory.model.RuleCategory;
import com.se_frms.ruleCategory.repository.RuleCategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudRuleServiceImpl
        implements FraudRuleService {

    private final FraudRuleRepository repository;

    private final RuleCategoryRepository categoryRepository;

    @Override
    public FraudRuleResponseDTO create(
            FraudRuleRequestDTO request
    ) {

        RuleCategory category =

                categoryRepository

                        .findById(
                                request.getCategoryId()
                        )

                        .orElseThrow(

                                () ->
                                        new InvalidRequestException(
                                                "Category not found"
                                        )

                        );

        FraudRule entity =

                FraudRule.builder()

                        .category(
                                category
                        )

                        .ruleCode(
                                request.getRuleCode()
                        )

                        .ruleName(
                                request.getRuleName()
                        )

                        .ruleDescription(
                                request.getRuleDescription()
                        )

                        .status(
                                true
                        )


                        .createdBy(
                        request.getCreatedBy()
                        )


                        .build();

        repository.save(
                entity
        );

        return map(
                entity
        );

    }

    @Override
    public FraudRuleResponseDTO update(

            Integer id,

            FraudRuleUpdateDTO request

    ) {

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

        RuleCategory category =

                categoryRepository

                        .findById(
                                request.getCategoryId()
                        )

                        .orElseThrow(

                                () ->
                                        new InvalidRequestException(
                                                "Category not found"
                                        )

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

        entity.setUpdatedAt(
                LocalDateTime.now()
        );

        repository.save(
                entity
        );

        return map(
                entity
        );

    }

    @Override
    public void delete(
            Integer id
    ) {

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

        return repository

                .findAll(
                        pageable
                )

                .map(
                        this::map
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

                .build();

    }

}
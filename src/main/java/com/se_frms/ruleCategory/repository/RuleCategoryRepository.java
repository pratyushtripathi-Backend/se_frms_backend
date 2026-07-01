package com.se_frms.ruleCategory.repository;

import com.se_frms.ruleCategory.model.RuleCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RuleCategoryRepository
        extends JpaRepository<RuleCategory, Integer>,
        JpaSpecificationExecutor<RuleCategory> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    Optional<RuleCategory> findByCategoryNameIgnoreCase(String categoryName);
}
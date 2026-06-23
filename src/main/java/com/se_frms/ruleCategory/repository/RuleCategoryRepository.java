package com.se_frms.ruleCategory.repository;

import com.se_frms.ruleCategory.model.RuleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuleCategoryRepository
        extends JpaRepository<RuleCategory, Integer> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    Optional<RuleCategory> findByCategoryNameIgnoreCase(String categoryName);
}
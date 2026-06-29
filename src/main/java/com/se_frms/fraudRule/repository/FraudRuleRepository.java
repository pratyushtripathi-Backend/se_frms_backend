package com.se_frms.fraudRule.repository;

import com.se_frms.fraudRule.model.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FraudRuleRepository
        extends JpaRepository<FraudRule,Integer> {
    List<FraudRule> findByCategoryId(
            Integer categoryId
    );

    boolean existsByRuleCodeAndRuleNameIgnoreCase(
            String ruleCode,
            String ruleName
    );

    Optional<FraudRule> findByRuleCodeOrRuleNameIgnoreCase(
            String ruleCode,
            String ruleName
    );

}
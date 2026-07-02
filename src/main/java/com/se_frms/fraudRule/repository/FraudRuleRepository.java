package com.se_frms.fraudRule.repository;

import com.se_frms.fraudRule.model.FraudRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FraudRuleRepository
        extends JpaRepository<FraudRule,Integer>,
        JpaSpecificationExecutor<FraudRule> {
    List<FraudRule> findByCategoryId(
            Integer categoryId
    );

    List<FraudRule> findByCategoryIdAndStatusTrue(
            Integer categoryId
    );

    Page<FraudRule> findByStatusTrue(
            Pageable pageable
    );

    Optional<FraudRule> findByIdAndStatusTrue(
            Integer id
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

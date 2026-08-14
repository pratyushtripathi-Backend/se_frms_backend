package com.se_frms.ruleScore.repository;

import com.se_frms.fraudRule.model.FraudRule;
import com.se_frms.ruleScore.model.RuleScore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface RuleScoreRepository
        extends JpaRepository<RuleScore, Integer>,
        JpaSpecificationExecutor<RuleScore> {

    boolean existsByRule(
            FraudRule rule
    );

    Optional<RuleScore> findByRule(
            FraudRule rule
    );

    @Query("""
        SELECT DISTINCT ruleScore
        FROM RuleScore ruleScore
        JOIN FETCH ruleScore.rule fraudRule
        JOIN FETCH fraudRule.category category
        WHERE ruleScore.status = true
          AND fraudRule.status = true
          AND category.status = true
        """)
    List<RuleScore> findActiveRuleScoresForCache();
}
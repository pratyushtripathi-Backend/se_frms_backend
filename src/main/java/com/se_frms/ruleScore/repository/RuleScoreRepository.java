package com.se_frms.ruleScore.repository;

import com.se_frms.fraudRule.model.FraudRule;
import com.se_frms.ruleScore.model.RuleScore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleScoreRepository
        extends JpaRepository<RuleScore, Integer> {

    boolean existsByRule(
            FraudRule rule
    );

    Optional<RuleScore> findByRule(
            FraudRule rule
    );

}
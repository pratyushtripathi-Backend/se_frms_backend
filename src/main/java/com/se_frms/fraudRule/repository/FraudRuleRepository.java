package com.se_frms.fraudRule.repository;

import com.se_frms.fraudRule.model.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudRuleRepository
        extends JpaRepository<FraudRule,Integer> {

}
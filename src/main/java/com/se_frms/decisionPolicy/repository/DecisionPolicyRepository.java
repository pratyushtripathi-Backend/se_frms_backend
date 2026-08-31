package com.se_frms.decisionPolicy.repository;

import com.se_frms.decisionPolicy.model.DecisionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DecisionPolicyRepository
        extends JpaRepository<DecisionPolicy, Integer>,
        JpaSpecificationExecutor<DecisionPolicy> {

    Optional<DecisionPolicy> findFirstByStatusTrueOrderByUpdatedAtDesc();

    Optional<DecisionPolicy> findFirstByOrderByCreatedAtDesc();
}

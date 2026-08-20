package com.se_frms.decisionPolicy.service;

import com.se_frms.decisionPolicy.dto.*;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DecisionPolicyService {

    DecisionPolicyResponseDTO createDecisionPolicy(
            DecisionPolicyRequestDTO request
    );

    DecisionPolicyResponseDTO updateDecisionPolicy(
            Integer id,
            DecisionPolicyRequestDTO request
    );

    DecisionPolicyResponseDTO updateStatus(
            Integer id,
            DecisionPolicyStatusRequestDTO request
    );

    void deleteDecisionPolicy(
            Integer id
    );

    Page<DecisionPolicyResponseDTO> getAllDecisionPolicies(
            Integer page,
            Integer size,
            Map<String, String> filters
    );

    DecisionPolicyResponseDTO getDecisionPolicyById(
            Integer id
    );
}

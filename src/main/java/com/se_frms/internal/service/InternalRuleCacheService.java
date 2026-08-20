package com.se_frms.internal.service;

import com.se_frms.internal.dto.RuleCacheSyncResponseDTO;
import com.se_frms.internal.dto.DecisionPolicyCacheResponseDTO;

import java.util.List;

public interface InternalRuleCacheService {

    List<RuleCacheSyncResponseDTO> getActiveRulesForCache();

    DecisionPolicyCacheResponseDTO getActiveDecisionPolicyForCache();
}

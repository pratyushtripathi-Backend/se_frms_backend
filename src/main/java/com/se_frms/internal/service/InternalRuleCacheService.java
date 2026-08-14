package com.se_frms.internal.service;

import com.se_frms.internal.dto.RuleCacheSyncResponseDTO;

import java.util.List;

public interface InternalRuleCacheService {

    List<RuleCacheSyncResponseDTO> getActiveRulesForCache();
}
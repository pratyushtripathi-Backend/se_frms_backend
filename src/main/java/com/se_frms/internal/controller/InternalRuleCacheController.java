package com.se_frms.internal.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.internal.dto.DecisionPolicyCacheResponseDTO;
import com.se_frms.internal.dto.RuleCacheSyncResponseDTO;
import com.se_frms.internal.dto.BlacklistCacheSyncResponseDTO;
import com.se_frms.internal.service.InternalRuleCacheService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/rule-cache")
@RequiredArgsConstructor
public class InternalRuleCacheController {

    private final InternalRuleCacheService internalRuleCacheService;

    @GetMapping("/active-rules")
    public ResponseEntity<AuthResponseDTO<List<RuleCacheSyncResponseDTO>>> getActiveRules() {
        List<RuleCacheSyncResponseDTO> responseData =
                internalRuleCacheService.getActiveRulesForCache();

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<RuleCacheSyncResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Active rules fetched for cache successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/active-blacklist-entries")
    public ResponseEntity<AuthResponseDTO<List<BlacklistCacheSyncResponseDTO>>> getActiveBlacklistEntries() {
        List<BlacklistCacheSyncResponseDTO> responseData =
                internalRuleCacheService.getActiveBlacklistEntriesForCache();

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<BlacklistCacheSyncResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Active blacklist entries fetched for cache successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/active-decision-policy")
    public ResponseEntity<AuthResponseDTO<DecisionPolicyCacheResponseDTO>> getActiveDecisionPolicy() {
        DecisionPolicyCacheResponseDTO responseData =
                internalRuleCacheService.getActiveDecisionPolicyForCache();

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<DecisionPolicyCacheResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Active decision policy fetched for cache successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

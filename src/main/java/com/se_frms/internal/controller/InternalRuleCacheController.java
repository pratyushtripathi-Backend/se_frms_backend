package com.se_frms.internal.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.internal.dto.RuleCacheSyncResponseDTO;
import com.se_frms.internal.service.InternalRuleCacheService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/rule-cache")
@RequiredArgsConstructor
public class InternalRuleCacheController {

    private final InternalRuleCacheService internalRuleCacheService;

    @Value("${app.internal.api-key:local-internal-key}")
    private String internalApiKey;

    @GetMapping("/active-rules")
    public ResponseEntity<AuthResponseDTO<List<RuleCacheSyncResponseDTO>>> getActiveRules(
            @RequestHeader(
                    value = "X-INTERNAL-API-KEY",
                    required = false
            )
            String apiKey
    ) {

        if (apiKey == null || !internalApiKey.equals(apiKey)) {
            throw new AccessDeniedException("Invalid internal API key");
        }

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
}
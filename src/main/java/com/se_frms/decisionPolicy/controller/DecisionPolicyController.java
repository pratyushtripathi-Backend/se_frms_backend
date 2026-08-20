package com.se_frms.decisionPolicy.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.common.dto.PagedResponseDTO;
import com.se_frms.decisionPolicy.dto.*;
import com.se_frms.decisionPolicy.service.DecisionPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/decision-policy")
@RequiredArgsConstructor
public class DecisionPolicyController {

    private final DecisionPolicyService decisionPolicyService;

    @PostMapping("/create")
    public ResponseEntity<AuthResponseDTO<DecisionPolicyResponseDTO>>
    createDecisionPolicy(
            @Valid
            @RequestBody
            DecisionPolicyRequestDTO request
    ) {

        DecisionPolicyResponseDTO responseData =
                decisionPolicyService.createDecisionPolicy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<DecisionPolicyResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage("Decision policy created successfully")
                                .responseData(responseData)
                                .build()
                );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AuthResponseDTO<DecisionPolicyResponseDTO>>
    updateDecisionPolicy(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            DecisionPolicyRequestDTO request
    ) {

        DecisionPolicyResponseDTO responseData =
                decisionPolicyService.updateDecisionPolicy(id, request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<DecisionPolicyResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Decision policy updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<AuthResponseDTO<DecisionPolicyResponseDTO>>
    updateStatus(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            DecisionPolicyStatusRequestDTO request
    ) {

        DecisionPolicyResponseDTO responseData =
                decisionPolicyService.updateStatus(id, request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<DecisionPolicyResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Decision policy status updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AuthResponseDTO<Object>>
    deleteDecisionPolicy(
            @PathVariable
            Integer id
    ) {

        decisionPolicyService.deleteDecisionPolicy(id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Decision policy deleted successfully")
                        .responseData(null)
                        .build()
        );
    }

    @GetMapping("/list")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<DecisionPolicyResponseDTO>>>
    getAllDecisionPolicies(
            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size,

            @RequestParam
            Map<String, String> filters
    ) {

        Page<DecisionPolicyResponseDTO> pageData =
                decisionPolicyService.getAllDecisionPolicies(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<DecisionPolicyResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<DecisionPolicyResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Decision policies fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<DecisionPolicyResponseDTO>>
    getDecisionPolicyById(
            @PathVariable
            Integer id
    ) {

        DecisionPolicyResponseDTO responseData =
                decisionPolicyService.getDecisionPolicyById(id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<DecisionPolicyResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Decision policy fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

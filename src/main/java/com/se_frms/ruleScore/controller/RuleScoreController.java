package com.se_frms.ruleScore.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.common.dto.PagedResponseDTO;
import com.se_frms.ruleScore.dto.*;
import com.se_frms.ruleScore.service.RuleScoreService;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/rule-score")
@RequiredArgsConstructor
public class RuleScoreController {

    private final RuleScoreService ruleScoreService;

    @PostMapping("/create")
    public ResponseEntity<AuthResponseDTO<RuleScoreResponseDTO>>
    createRuleScore(
            @Valid
            @RequestBody
            RuleScoreRequestDTO request
    ) {

        RuleScoreResponseDTO responseData =
                ruleScoreService.createRuleScore(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<RuleScoreResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage("Rule score created successfully")
                                .responseData(responseData)
                                .build()
                );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AuthResponseDTO<RuleScoreResponseDTO>>
    updateRuleScore(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            RuleScoreRequestDTO request
    ) {

        RuleScoreResponseDTO responseData =
                ruleScoreService.updateRuleScore(id, request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RuleScoreResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Rule score updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<AuthResponseDTO<RuleScoreResponseDTO>>
    updateStatus(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            RuleScoreStatusRequestDTO request
    ) {

        RuleScoreResponseDTO responseData =
                ruleScoreService.updateStatus(id, request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RuleScoreResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Rule score status updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AuthResponseDTO<Object>>
    deleteRuleScore(
            @PathVariable
            Integer id
    ) {

        ruleScoreService.deleteRuleScore(id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Rule score deleted successfully")
                        .responseData(null)
                        .build()
        );
    }

//    @GetMapping("/list")
//    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<RuleScoreResponseDTO>>>
//    getAllRuleScores(
//            @RequestParam(required = false)
//            Integer page,
//
//            @RequestParam(required = false)
//            Integer size,
//
//            @RequestParam
//            Map<String, String> filters
//    ) {
//
//        Page<RuleScoreResponseDTO> pageData =
//                ruleScoreService.getAllRuleScores(
//                        page,
//                        size,
//                        filters
//                );
//
//        PagedResponseDTO<RuleScoreResponseDTO> responseData =
//                PagedResponseDTO.from(pageData);
//
//        return ResponseEntity.ok(
//                AuthResponseDTO
//                        .<PagedResponseDTO<RuleScoreResponseDTO>>builder()
//                        .status(true)
//                        .responseCode(200)
//                        .responseMessage("Rule scores fetched successfully")
//                        .responseData(responseData)
//                        .build()
//        );
//    }

    @GetMapping("/list")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<RuleScoreResponseDTO>>>
    getAllRuleScores(
            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size,

            HttpServletRequest httpRequest
    ) {

        Map<String, String> filters =
                httpRequest
                        .getParameterMap()
                        .entrySet()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry ->
                                                entry.getValue() != null
                                                        && entry.getValue().length > 0
                                                        ? entry.getValue()[0]
                                                        : ""
                                )
                        );

        Page<RuleScoreResponseDTO> pageData =
                ruleScoreService.getAllRuleScores(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<RuleScoreResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<RuleScoreResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Rule scores fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<RuleScoreResponseDTO>>
    getRuleScoreById(
            @PathVariable
            Integer id
    ) {

        RuleScoreResponseDTO responseData =
                ruleScoreService.getRuleScoreById(id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RuleScoreResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Rule score fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<AuthResponseDTO<RuleScoreResponseDTO>>
    getRuleScoreByRuleId(
            @PathVariable
            Integer ruleId
    ) {

        RuleScoreResponseDTO responseData =
                ruleScoreService.getRuleScoreByRuleId(ruleId);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RuleScoreResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Rule score fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}
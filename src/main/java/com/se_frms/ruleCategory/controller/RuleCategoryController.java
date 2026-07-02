package com.se_frms.ruleCategory.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.common.dto.PagedResponseDTO;
import com.se_frms.ruleCategory.dto.*;
import com.se_frms.ruleCategory.service.RuleCategoryService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/rule-category")
@RequiredArgsConstructor
public class RuleCategoryController {

    private final RuleCategoryService ruleCategoryService;

    @PostMapping("/create")
    public ResponseEntity<AuthResponseDTO<RuleCategoryResponseDTO>>
    createCategory(
            @Valid
            @RequestBody
            RuleCategoryRequestDTO request
    ) {

        RuleCategoryResponseDTO responseData =
                ruleCategoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<RuleCategoryResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage("Category created successfully")
                                .responseData(responseData)
                                .build()
                );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AuthResponseDTO<RuleCategoryResponseDTO>>
    updateCategory(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            RuleCategoryRequestDTO request
    ) {

        RuleCategoryResponseDTO responseData =
                ruleCategoryService.updateCategory(id, request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RuleCategoryResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Category updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<AuthResponseDTO<RuleCategoryResponseDTO>>
    updateStatus(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            RuleCategoryStatusRequestDTO request
    ) {

        RuleCategoryResponseDTO responseData =
                ruleCategoryService.updateStatus(id, request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RuleCategoryResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Category status updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AuthResponseDTO<Object>>
    deleteCategory(
            @PathVariable
            Integer id
    ) {

        ruleCategoryService.deleteCategory(id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Category deleted successfully")
                        .responseData(null)
                        .build()
        );
    }

    @GetMapping("/list")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<RuleCategoryResponseDTO>>>
    getAllCategories(
            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size,

            @RequestParam
            Map<String, String> filters
    ) {

        Page<RuleCategoryResponseDTO> pageData =
                ruleCategoryService.getAllCategories(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<RuleCategoryResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<RuleCategoryResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Categories fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<RuleCategoryResponseDTO>>
    getCategoryById(
            @PathVariable
            Integer id
    ) {

        RuleCategoryResponseDTO responseData =
                ruleCategoryService.getCategoryById(id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RuleCategoryResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Category fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

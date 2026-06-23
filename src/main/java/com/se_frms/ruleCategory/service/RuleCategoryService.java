package com.se_frms.ruleCategory.service;

import com.se_frms.ruleCategory.dto.*;
import org.springframework.data.domain.Page;

public interface RuleCategoryService {

    RuleCategoryResponseDTO createCategory(
            RuleCategoryRequestDTO request
    );

    RuleCategoryResponseDTO updateCategory(
            Integer id,
            RuleCategoryRequestDTO request
    );

    RuleCategoryResponseDTO updateStatus(
            Integer id,
            RuleCategoryStatusRequestDTO request
    );

    void deleteCategory(
            Integer id
    );

    Page<RuleCategoryResponseDTO> getAllCategories(
            Integer page,
            Integer size
    );

    RuleCategoryResponseDTO getCategoryById(
            Integer id
    );
}
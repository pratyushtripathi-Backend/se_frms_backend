package com.se_frms.fraudRule.service;

import com.se_frms.fraudRule.dto.FraudRuleRequestDTO;
import com.se_frms.fraudRule.dto.FraudRuleResponseDTO;
import com.se_frms.fraudRule.dto.FraudRuleStatusDTO;
import com.se_frms.fraudRule.dto.FraudRuleUpdateDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FraudRuleService {

    FraudRuleResponseDTO create(
            FraudRuleRequestDTO request
    );

    FraudRuleResponseDTO update(
            Integer id,
            FraudRuleUpdateDTO request
    );

    void delete(
            Integer id
    );

    FraudRuleResponseDTO getById(
            Integer id
    );

    Page<FraudRuleResponseDTO> getAll(
            Pageable pageable
    );

    List<FraudRuleResponseDTO> getByCategoryId(
            Integer categoryId
    );

    FraudRuleResponseDTO updateStatus(

            Integer id,

            FraudRuleStatusDTO request

    );

}
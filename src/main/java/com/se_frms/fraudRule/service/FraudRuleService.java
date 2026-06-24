package com.se_frms.fraudRule.service;

import com.se_frms.fraudRule.dto.FraudRuleRequestDTO;
import com.se_frms.fraudRule.dto.FraudRuleResponseDTO;
import com.se_frms.fraudRule.dto.FraudRuleUpdateDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

}
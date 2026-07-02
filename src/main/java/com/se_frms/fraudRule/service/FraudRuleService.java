package com.se_frms.fraudRule.service;

import com.se_frms.fraudRule.dto.FraudRuleRequestDTO;
import com.se_frms.fraudRule.dto.FraudRuleResponseDTO;
import com.se_frms.fraudRule.dto.FraudRuleStatusDTO;
import com.se_frms.fraudRule.dto.FraudRuleUpdateDTO;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

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
            int page,
            int size,
            Map<String, String> filters
    );

    List<FraudRuleResponseDTO> getByCategoryId(
            Integer categoryId
    );

    FraudRuleResponseDTO updateStatus(

            Integer id,

            FraudRuleStatusDTO request

    );

}

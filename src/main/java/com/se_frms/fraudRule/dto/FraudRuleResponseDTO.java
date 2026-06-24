package com.se_frms.fraudRule.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRuleResponseDTO {

    private Integer id;

    private Integer categoryId;

    private String categoryName;

    private String ruleCode;

    private String ruleName;

    private String ruleDescription;

    private Boolean status;

    private Integer createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
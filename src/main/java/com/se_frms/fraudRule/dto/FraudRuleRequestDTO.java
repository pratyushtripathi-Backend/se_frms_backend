package com.se_frms.fraudRule.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRuleRequestDTO {

    private Integer categoryId;

    private String ruleCode;

    private String ruleName;

    private String ruleDescription;

    private Integer createdBy;

}

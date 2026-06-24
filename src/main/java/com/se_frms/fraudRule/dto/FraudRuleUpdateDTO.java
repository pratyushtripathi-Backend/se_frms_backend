package com.se_frms.fraudRule.dto;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRuleUpdateDTO {

    private Integer categoryId;

    private String ruleCode;

    private String ruleName;

    private String ruleDescription;

    private Boolean status;

}

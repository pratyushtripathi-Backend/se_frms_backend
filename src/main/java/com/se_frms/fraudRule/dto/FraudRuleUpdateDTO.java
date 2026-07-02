package com.se_frms.fraudRule.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRuleUpdateDTO {

    @NotNull(message = "Category id is required")
    private Integer categoryId;

    @NotBlank(message = "Rule code is required")
    @Size(max = 100, message = "Rule code must not exceed 100 characters")
    private String ruleCode;

    @NotBlank(message = "Rule name is required")
    @Size(max = 150, message = "Rule name must not exceed 150 characters")
    private String ruleName;

    @Size(max = 500, message = "Rule description must not exceed 500 characters")
    private String ruleDescription;

    @NotNull(message = "Status is required")
    private Boolean status;

}

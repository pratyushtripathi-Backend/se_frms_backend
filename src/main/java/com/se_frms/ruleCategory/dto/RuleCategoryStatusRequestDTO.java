package com.se_frms.ruleCategory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleCategoryStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}
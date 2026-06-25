package com.se_frms.ruleScore.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleScoreStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}
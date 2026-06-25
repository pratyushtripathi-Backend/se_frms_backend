package com.se_frms.ruleScore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleScoreRequestDTO {

    @NotNull(message = "Rule id is required")
    private Integer ruleId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be greater than or equal to 0")
    @Max(value = 100, message = "Score must be less than or equal to 100")
    private Integer score;

    private Boolean status;
}
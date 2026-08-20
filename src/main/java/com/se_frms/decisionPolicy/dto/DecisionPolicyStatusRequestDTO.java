package com.se_frms.decisionPolicy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecisionPolicyStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}

package com.se_frms.fraudRule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRuleStatusDTO {

    @NotNull(message = "Status is required")
    private Boolean status;

}

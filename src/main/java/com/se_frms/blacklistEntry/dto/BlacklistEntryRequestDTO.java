package com.se_frms.blacklistEntry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlacklistEntryRequestDTO {

    @NotBlank(message = "type is required (IP, DEVICE or LOCATION)")
    private String type;

    @NotBlank(message = "value is required")
    private String value;

    private String reason;

    private String riskType;
}

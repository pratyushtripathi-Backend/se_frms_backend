package com.se_frms.blacklistEntry.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Partial update for an existing blacklist entry.
 * A field that is not sent (null) is left unchanged.
 */
@Getter
@Setter
public class BlacklistEntryUpdateDTO {

    // IP / DEVICE / LOCATION
    @Size(max = 20, message = "type must not exceed 20 characters")
    private String type;

    @Size(max = 255, message = "value must not exceed 255 characters")
    private String value;

    @Size(max = 500, message = "reason must not exceed 500 characters")
    private String reason;

    @Size(max = 100, message = "riskType must not exceed 100 characters")
    private String riskType;
}

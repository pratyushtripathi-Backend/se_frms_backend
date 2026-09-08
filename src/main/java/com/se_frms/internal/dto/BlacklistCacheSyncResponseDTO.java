package com.se_frms.internal.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistCacheSyncResponseDTO {

    private Integer blacklistId;

    // IP / DEVICE / LOCATION
    private String type;

    private String value;

    private Boolean status;

    private String createdBy;
}

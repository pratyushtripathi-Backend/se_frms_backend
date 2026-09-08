package com.se_frms.blacklistEntry.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BlacklistEntryResponseDTO {

    private Integer id;

    private String type;

    private String value;

    private String reason;

    private String riskType;

    private Boolean status;

    private String createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}

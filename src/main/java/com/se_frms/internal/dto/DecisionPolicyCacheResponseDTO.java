package com.se_frms.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionPolicyCacheResponseDTO {

    private Integer policyId;

    private String description;

    private Integer allowMinScore;

    private Integer allowMaxScore;

    private Integer reviewMinScore;

    private Integer reviewMaxScore;

    private Integer blockMinScore;

    private Integer blockMaxScore;

    private Boolean status;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

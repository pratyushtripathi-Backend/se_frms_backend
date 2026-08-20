package com.se_frms.decisionPolicy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionPolicyResponseDTO {

    private Integer id;

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

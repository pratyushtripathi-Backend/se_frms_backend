package com.se_frms.ruleScore.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleScoreResponseDTO {

    private Integer id;

    private Integer ruleId;

    private String ruleCode;

    private String ruleName;

    private Integer score;

    private Boolean status;

    private Integer createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
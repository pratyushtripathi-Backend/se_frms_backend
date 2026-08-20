package com.se_frms.decisionPolicy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecisionPolicyRequestDTO {

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    @NotNull(message = "Allow minimum score is required")
    @Min(value = 0, message = "Allow minimum score must be 0 or greater")
    private Integer allowMinScore;

    @NotNull(message = "Allow maximum score is required")
    @Min(value = 0, message = "Allow maximum score must be 0 or greater")
    private Integer allowMaxScore;

    @NotNull(message = "Review minimum score is required")
    @Min(value = 0, message = "Review minimum score must be 0 or greater")
    private Integer reviewMinScore;

    @NotNull(message = "Review maximum score is required")
    @Min(value = 0, message = "Review maximum score must be 0 or greater")
    private Integer reviewMaxScore;

    @NotNull(message = "Block minimum score is required")
    @Min(value = 0, message = "Block minimum score must be 0 or greater")
    private Integer blockMinScore;

    @NotNull(message = "Block maximum score is required")
    @Min(value = 0, message = "Block maximum score must be 0 or greater")
    private Integer blockMaxScore;

    private Boolean status;
}

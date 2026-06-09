package com.se_frms.access.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccessResponseDTO {

    private Integer id;

    private String accessName;

    private Boolean status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;

}
package com.se_frms.blackListUser.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlackListUserResponseDTO {

    private Integer id;

    private Integer userId;

    private String employeeName;

    private String email;

    private String mobile;

    private Boolean status;

    private String reason;

    private String riskType;

    private Integer createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}

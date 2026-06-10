package com.se_frms.roleMaster.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RoleMasterResponseDTO {

    private Integer roleId;

    private String roleName;

    private Boolean status;

    private Integer createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}
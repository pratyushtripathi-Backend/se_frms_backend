package com.se_frms.roleAccess.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class RoleAccessResponseDTO {

    private Integer id;

    private Integer roleId;

    private String roleName;

    private Integer accessId;

    private String accessName;

    private Boolean status;

    private String createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}
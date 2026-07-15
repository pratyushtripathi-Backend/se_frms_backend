package com.se_frms.roleAccess.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class RoleAccessResponseDTO {

    private Integer id;

    private Integer roleId;

    private String roleName;

    private Integer accessId;

    private List<String> accessNames;

    private Boolean status;

    private Integer createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}
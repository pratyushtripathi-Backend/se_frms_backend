package com.se_frms.userRole.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserRoleResponseDTO {

    private Integer id;

    private Integer userId;

    private String name;

    private String email;

    private Integer roleId;

    private String roleName;

    private Boolean status;

    private Integer createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}

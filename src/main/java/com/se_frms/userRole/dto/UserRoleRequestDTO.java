package com.se_frms.userRole.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRoleRequestDTO {

    @NotNull(message = "User id is required")
    private Integer userId;

    @NotBlank(message = "Role name is required")
    private String roleName;
}

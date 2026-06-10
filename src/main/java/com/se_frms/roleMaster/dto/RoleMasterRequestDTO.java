package com.se_frms.roleMaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleMasterRequestDTO {

    @NotBlank(message = "Role name is required")
    private String roleName;

    private Boolean status;

    private Integer createdBy;
}
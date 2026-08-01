package com.se_frms.roleAccess.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleAccessStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}
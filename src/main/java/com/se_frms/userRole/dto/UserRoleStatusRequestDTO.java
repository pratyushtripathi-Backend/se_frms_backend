package com.se_frms.userRole.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRoleStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}
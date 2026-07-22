package com.se_frms.roleMaster.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleMasterStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}
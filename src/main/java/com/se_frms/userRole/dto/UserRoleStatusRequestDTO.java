package com.se_frms.userRole.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class UserRoleStatusRequestDTO {

    private Boolean status;

    private String roleName;

    @AssertTrue(message = "Status or role name is required")
    public boolean isValidRequest() {
        return status != null
                || (roleName != null && !roleName.isBlank());
    }
}
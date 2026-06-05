package com.se_frms.permission.dto;



import com.se_frms.permission.enums.Permission;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatePermissionRequest {

    @NotEmpty(
            message =
                    "Permissions are required"
    )
    private List<Permission>
            permissions;
}

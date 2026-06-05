package com.se_frms.permission.service;



import com.se_frms.permission.dto.GrantPermissionRequest;
import com.se_frms.permission.dto.PermissionResponseDTO;
import com.se_frms.permission.dto.UpdatePermissionRequest;

import java.util.UUID;

public interface PermissionService {

    PermissionResponseDTO grantPermissions(
            UUID employeeId,
            GrantPermissionRequest request
    );

    PermissionResponseDTO updatePermissions(
            UUID employeeId,
            UpdatePermissionRequest request
    );

    PermissionResponseDTO getPermissions(
            UUID employeeId
    );

    void revokePermission(
            UUID employeeId
    );
}

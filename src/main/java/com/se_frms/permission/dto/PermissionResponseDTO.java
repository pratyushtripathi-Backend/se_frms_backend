package com.se_frms.permission.dto;



import com.se_frms.permission.enums.Permission;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PermissionResponseDTO {

    private UUID userId;

    private String employeeName;

    private List<Permission>
            permissions;
}

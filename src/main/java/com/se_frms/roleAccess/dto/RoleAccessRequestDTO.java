package com.se_frms.roleAccess.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleAccessRequestDTO {

    @NotNull(message = "Role id is required")
    private Integer roleId;

    @NotEmpty(message = "Access ids are required")
    private List<Integer> accessIds;
}
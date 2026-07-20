package com.se_frms.roleAccess.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleAccessUpdateRequestDTO {

    @NotNull(message = "Access ids are required")
    private List<Integer> accessIds;
}
package com.se_frms.roleAccess.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RoleAccessResponseDTO {

    private Integer roleId;

    private String roleName;

    private List<String> accessNames;

}

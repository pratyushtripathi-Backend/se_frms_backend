package com.se_frms.roleAccess.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleAccessRequestDTO {

    private Integer roleId;

    private List<Integer> accessIds;

}
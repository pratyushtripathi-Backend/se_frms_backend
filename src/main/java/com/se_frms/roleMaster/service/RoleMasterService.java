package com.se_frms.roleMaster.service;

import com.se_frms.roleMaster.dto.RoleMasterRequestDTO;
import com.se_frms.roleMaster.dto.RoleMasterResponseDTO;

import java.util.List;

public interface RoleMasterService {

    RoleMasterResponseDTO createRole(RoleMasterRequestDTO request);

    List<RoleMasterResponseDTO> getAllRoles();

    List<RoleMasterResponseDTO> getActiveRoles();

    RoleMasterResponseDTO getRoleById(Integer roleId);

    RoleMasterResponseDTO updateRole(
            Integer roleId,
            RoleMasterRequestDTO request
    );
}
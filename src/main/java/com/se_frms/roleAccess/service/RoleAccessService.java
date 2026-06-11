package com.se_frms.roleAccess.service;

import com.se_frms.roleAccess.dto.RoleAccessRequestDTO;
import com.se_frms.roleAccess.dto.RoleAccessResponseDTO;

import java.util.List;

public interface RoleAccessService {

    RoleAccessResponseDTO create(
            RoleAccessRequestDTO request
    );

    List<RoleAccessResponseDTO> getAll();

    RoleAccessResponseDTO getByRole(

            Integer roleId

    );

    String updateAccessStatus(

            Integer roleId,

            Integer accessId,

            Boolean status

    );

}

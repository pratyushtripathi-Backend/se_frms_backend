package com.se_frms.roleMaster.service;

import com.se_frms.roleMaster.dto.RoleMasterRequestDTO;
import com.se_frms.roleMaster.dto.RoleMasterResponseDTO;
import org.springframework.data.domain.Page;
import java.util.List;

public interface RoleMasterService {

    RoleMasterResponseDTO createRole(RoleMasterRequestDTO request);


    Page<RoleMasterResponseDTO> getAllRoles(
            Integer page,
            Integer size
    );
    List<RoleMasterResponseDTO> getActiveRoles();

    RoleMasterResponseDTO getRoleById(Integer roleId);

    RoleMasterResponseDTO updateRole(
            Integer roleId,
            RoleMasterRequestDTO request
    );
}
package com.se_frms.roleMaster.service;

import com.se_frms.roleMaster.dto.RoleMasterRequestDTO;
import com.se_frms.roleMaster.dto.RoleMasterResponseDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

public interface RoleMasterService {

    RoleMasterResponseDTO createRole(RoleMasterRequestDTO request);


    Page<RoleMasterResponseDTO> getAllRoles(
        int page,
        int size,
        Map<String, String> filters
    );

    List<RoleMasterResponseDTO> getActiveRoles();

    RoleMasterResponseDTO getRoleById(Integer roleId);

    RoleMasterResponseDTO updateRole(
            Integer roleId,
            RoleMasterRequestDTO request
    );
}

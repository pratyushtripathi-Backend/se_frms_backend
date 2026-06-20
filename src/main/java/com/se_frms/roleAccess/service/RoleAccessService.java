package com.se_frms.roleAccess.service;

import com.se_frms.roleAccess.dto.RoleAccessRequestDTO;
import com.se_frms.roleAccess.dto.RoleAccessResponseDTO;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface RoleAccessService {

    RoleAccessResponseDTO create(
            RoleAccessRequestDTO request
    );

    Page<RoleAccessResponseDTO> getAll(
            int page,
            int size,
            Map<String, String> filters
    );

    RoleAccessResponseDTO getByRole(

            Integer roleId

    );

    String updateAccessStatus(

            Integer roleId,

            Integer accessId,

            Boolean status

    );

}

package com.se_frms.userRole.service;

import com.se_frms.userRole.dto.UserRoleRequestDTO;
import com.se_frms.userRole.dto.UserRoleResponseDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface UserRoleService {

    UserRoleResponseDTO assignRole(UserRoleRequestDTO request);


    Page<UserRoleResponseDTO> getAllUserRoles(
            Integer page,
            Integer size
    );
    List<UserRoleResponseDTO> getActiveUserRoles();

    List<UserRoleResponseDTO> getRolesByUser(Integer userId);

    UserRoleResponseDTO updateStatus(Integer id, Boolean status);
}
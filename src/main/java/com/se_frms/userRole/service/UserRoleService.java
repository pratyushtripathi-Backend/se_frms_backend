package com.se_frms.userRole.service;

import com.se_frms.userRole.dto.UserRoleRequestDTO;
import com.se_frms.userRole.dto.UserRoleResponseDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
import com.se_frms.userRole.dto.UserRoleStatusRequestDTO;
public interface UserRoleService {

    UserRoleResponseDTO assignRole(UserRoleRequestDTO request);


   Page<UserRoleResponseDTO> getAllUserRoles(
           int page,
           int size,
           Map<String, String> filters
   );
    List<UserRoleResponseDTO> getActiveUserRoles();

    List<UserRoleResponseDTO> getRolesByUser(Integer userId);

    UserRoleResponseDTO updateStatus(
            Integer id,
            UserRoleStatusRequestDTO request
    );
}

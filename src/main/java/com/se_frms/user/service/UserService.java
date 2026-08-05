package com.se_frms.user.service;



import com.se_frms.user.dto.UserResponseDTO;
import com.se_frms.user.dto.UpdateUserRequest;

import org.springframework.data.domain.Page;

import java.lang.Integer;
import java.util.Map;
import com.se_frms.user.dto.UserStatusRequestDTO;
public interface UserService {

    Page<UserResponseDTO> getAllUsers(
            int page,
            int size,
            Map<String, String> filters
    );

    UserResponseDTO getUserById(Integer id);

    UserResponseDTO updateUser(
            Integer id,
            UpdateUserRequest request
    );

    UserResponseDTO updateUserStatus(
            Integer id,
            UserStatusRequestDTO request
    );
    Page<UserResponseDTO> getAllNonAdminUsers(
            int page,
            int size,
            Map<String, String> filters
    );
}

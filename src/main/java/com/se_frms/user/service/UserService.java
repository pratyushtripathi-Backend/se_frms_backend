package com.se_frms.user.service;



import com.se_frms.user.dto.UserResponseDTO;
import com.se_frms.user.dto.UpdateUserRequest;
import com.se_frms.user.dto.ProfileImageData;

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
    /** Saves (or replaces) the user's profile image. Only the user or an ADMIN may do this. */
    UserResponseDTO uploadProfileImage(
            Integer id,
            byte[] image
    );

    ProfileImageData getProfileImage(Integer id);

    /** Removes the user's profile image. Only the user or an ADMIN may do this. */
    UserResponseDTO deleteProfileImage(Integer id);

    Page<UserResponseDTO> getAllNonAdminUsers(
            int page,
            int size,
            Map<String, String> filters
    );
}

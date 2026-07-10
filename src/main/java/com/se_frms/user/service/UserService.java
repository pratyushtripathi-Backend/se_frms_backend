package com.se_frms.user.service;



import com.se_frms.user.dto.UserResponseDTO;
import com.se_frms.user.dto.UpdateUserRequest;

import java.lang.Integer;

public interface UserService {

    UserResponseDTO getUserById(Integer id);

    UserResponseDTO updateUser(
            Integer id,
            UpdateUserRequest request
    );
}

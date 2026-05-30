package com.se_frms.user.service;



import com.se_frms.user.dto.UserResponseDTO;

import java.util.UUID;

public interface UserService {

    UserResponseDTO getUserById(UUID id);
}

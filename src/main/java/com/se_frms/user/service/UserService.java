package com.se_frms.user.service;



import com.se_frms.user.dto.UserResponseDTO;

import java.lang.Integer;

public interface UserService {

    UserResponseDTO getUserById(Integer id);
}

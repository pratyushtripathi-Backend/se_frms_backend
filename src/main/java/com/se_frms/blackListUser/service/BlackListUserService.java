package com.se_frms.blackListUser.service;

import com.se_frms.blackListUser.dto.BlackListUserRequestDTO;
import com.se_frms.blackListUser.dto.BlackListUserResponseDTO;
import com.se_frms.blackListUser.dto.RemoveBlackListRequestDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface BlackListUserService {

    BlackListUserResponseDTO blackListUser(
            BlackListUserRequestDTO request
    );

    BlackListUserResponseDTO removeBlackList(
            RemoveBlackListRequestDTO request
    );

    Page<BlackListUserResponseDTO> getAllBlackListUsers(
            int page,
            int size,
            Map<String, String> filters
    );

    Page<BlackListUserResponseDTO> getBlackListUsersByUserId(
            Integer userId,
            int page,
            int size,
            Map<String, String> filters
    );

    BlackListUserResponseDTO getBlackListUserById(
            Integer id
    );
}

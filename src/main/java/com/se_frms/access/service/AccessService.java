package com.se_frms.access.service;

import com.se_frms.access.dto.AccessRequestDTO;
import com.se_frms.access.dto.AccessResponseDTO;

import java.util.List;

public interface AccessService {

    AccessResponseDTO
    create(
            AccessRequestDTO request
    );

    List<AccessResponseDTO>
    getAll();

    AccessResponseDTO getById(
            Integer id
    );

    AccessResponseDTO update(
            Integer id,
            AccessRequestDTO request
    );

    String updateStatus(
            Integer id,
            Boolean status
    );

    String delete(
            Integer id
    );

}

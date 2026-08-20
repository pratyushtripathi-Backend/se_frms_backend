package com.se_frms.access.service;

import com.se_frms.access.dto.AccessRequestDTO;
import com.se_frms.access.dto.AccessResponseDTO;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface AccessService {

    AccessResponseDTO
    create(
            AccessRequestDTO request
    );

    Page<AccessResponseDTO>
    getAll(
            int page,
            int size,
            Map<String, String> filters
    );

    List<AccessResponseDTO>
    getAllForDropdown(
            Map<String, String> filters
    );

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

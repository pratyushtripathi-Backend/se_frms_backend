package com.se_frms.roleAccess.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.roleAccess.dto.RoleAccessRequestDTO;
import com.se_frms.roleAccess.dto.RoleAccessResponseDTO;
import com.se_frms.roleAccess.service.RoleAccessService;
import com.se_frms.common.dto.PagedResponseDTO;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role-access")
@RequiredArgsConstructor
public class RoleAccessController {

    private final RoleAccessService service;

    @PostMapping
    public ResponseEntity<AuthResponseDTO<RoleAccessResponseDTO>> create(
            @RequestBody
            RoleAccessRequestDTO request
    ) {

        RoleAccessResponseDTO responseData =
                service.create(request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RoleAccessResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role access created successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<RoleAccessResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        Page<RoleAccessResponseDTO> pageData =
                service.getAll(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<RoleAccessResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<RoleAccessResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role access fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<AuthResponseDTO<RoleAccessResponseDTO>> getByRole(
            @PathVariable
            Integer roleId
    ) {

        RoleAccessResponseDTO responseData =
                service.getByRole(roleId);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RoleAccessResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role access fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/{roleId}/{accessId}/status")
    public ResponseEntity<AuthResponseDTO<String>> updateStatus(
            @PathVariable
            Integer roleId,

            @PathVariable
            Integer accessId,

            @RequestParam
            Boolean status
    ) {

        String responseData =
                service.updateAccessStatus(
                        roleId,
                        accessId,
                        status
                );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<String>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role access status updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

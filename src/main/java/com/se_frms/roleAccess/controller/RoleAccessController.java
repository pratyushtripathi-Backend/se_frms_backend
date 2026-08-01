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
import com.se_frms.roleAccess.dto.RoleAccessUpdateRequestDTO;
import jakarta.validation.Valid;
import com.se_frms.roleAccess.dto.RoleAccessStatusRequestDTO;
import java.util.List;
@RestController
@RequestMapping("/api/v1/role-access")
@RequiredArgsConstructor
public class RoleAccessController {

    private final RoleAccessService service;

    @PostMapping
    public ResponseEntity<AuthResponseDTO<List<RoleAccessResponseDTO>>> create(
            @Valid
            @RequestBody
            RoleAccessRequestDTO request
    ) {

        List<RoleAccessResponseDTO> responseData =
                service.create(request);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<RoleAccessResponseDTO>>builder()
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
    public ResponseEntity<AuthResponseDTO<List<RoleAccessResponseDTO>>> getByRole(
            @PathVariable Integer roleId
    ) {

        List<RoleAccessResponseDTO> responseData =
                service.getByRole(roleId);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<RoleAccessResponseDTO>>builder()
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

    @PutMapping("/{roleId}")
    public ResponseEntity<AuthResponseDTO<List<RoleAccessResponseDTO>>> updateRoleAccess(
            @PathVariable
            Integer roleId,

            @Valid
            @RequestBody
            RoleAccessUpdateRequestDTO request
    ) {

        List<RoleAccessResponseDTO> responseData =
                service.updateRoleAccess(
                        roleId,
                        request
                );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<RoleAccessResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role access updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }
    @PatchMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<RoleAccessResponseDTO>> updateStatusById(
            @PathVariable Integer id,

            @Valid
            @RequestBody RoleAccessStatusRequestDTO request
    ) {

        RoleAccessResponseDTO responseData =
                service.updateStatusById(
                        id,
                        request.getStatus()
                );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RoleAccessResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role access status updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

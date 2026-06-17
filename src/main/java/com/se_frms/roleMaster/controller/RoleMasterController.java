package com.se_frms.roleMaster.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.roleMaster.dto.RoleMasterRequestDTO;
import com.se_frms.roleMaster.dto.RoleMasterResponseDTO;
import com.se_frms.roleMaster.service.RoleMasterService;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import com.se_frms.common.dto.PagedResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
public class RoleMasterController {

    private final RoleMasterService roleMasterService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO<RoleMasterResponseDTO>>
    createRole(
            @Valid
            @RequestBody
            RoleMasterRequestDTO request
    ) {

        log.info("Create role request received");

        RoleMasterResponseDTO responseData =
                roleMasterService.createRole(request);

        log.info("Role created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<RoleMasterResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage("Role created successfully")
                                .responseData(responseData)
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<RoleMasterResponseDTO>>>
    getAllRoles(
            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size
    ) {

        Page<RoleMasterResponseDTO> pageData =
                roleMasterService.getAllRoles(page, size);

        PagedResponseDTO<RoleMasterResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<RoleMasterResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Roles fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }
    @GetMapping("/active")
    public ResponseEntity<AuthResponseDTO<List<RoleMasterResponseDTO>>>
    getActiveRoles() {

        log.info("Fetch active roles request received");

        List<RoleMasterResponseDTO> responseData =
                roleMasterService.getActiveRoles();

        log.info("Active roles fetched successfully, count={}", responseData.size());

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<RoleMasterResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Active roles fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<AuthResponseDTO<RoleMasterResponseDTO>>
    getRoleById(
            @PathVariable
            Integer roleId
    ) {

        log.info("Fetch role request received, roleId={}", roleId);

        RoleMasterResponseDTO responseData =
                roleMasterService.getRoleById(roleId);

        log.info("Role fetched successfully, roleId={}", roleId);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RoleMasterResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<AuthResponseDTO<RoleMasterResponseDTO>>
    updateRole(
            @PathVariable
            Integer roleId,

            @Valid
            @RequestBody
            RoleMasterRequestDTO request
    ) {

        log.info("Update role request received, roleId={}", roleId);

        RoleMasterResponseDTO responseData =
                roleMasterService.updateRole(roleId, request);

        log.info("Role updated successfully, roleId={}", roleId);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<RoleMasterResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Role updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

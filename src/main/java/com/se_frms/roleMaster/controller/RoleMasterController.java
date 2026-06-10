package com.se_frms.roleMaster.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.roleMaster.dto.RoleMasterRequestDTO;
import com.se_frms.roleMaster.dto.RoleMasterResponseDTO;
import com.se_frms.roleMaster.service.RoleMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        RoleMasterResponseDTO responseData =
                roleMasterService.createRole(request);

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
    public ResponseEntity<AuthResponseDTO<List<RoleMasterResponseDTO>>>
    getAllRoles() {

        List<RoleMasterResponseDTO> responseData =
                roleMasterService.getAllRoles();

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<RoleMasterResponseDTO>>builder()
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

        List<RoleMasterResponseDTO> responseData =
                roleMasterService.getActiveRoles();

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

        RoleMasterResponseDTO responseData =
                roleMasterService.getRoleById(roleId);

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

        RoleMasterResponseDTO responseData =
                roleMasterService.updateRole(roleId, request);

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
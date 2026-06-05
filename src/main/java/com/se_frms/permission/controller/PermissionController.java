package com.se_frms.permission.controller;


import com.se_frms.auth.dto.AuthResponseDTO;

import com.se_frms.permission.dto.GrantPermissionRequest;
import com.se_frms.permission.dto.PermissionResponseDTO;
import com.se_frms.permission.dto.UpdatePermissionRequest;

import com.se_frms.permission.service.PermissionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/permissions"
)
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService
            permissionService;

    @PostMapping(
            "/employees/{employeeId}"
    )
    public ResponseEntity<
            AuthResponseDTO<
                    PermissionResponseDTO
                    >
            >
    grantPermissions(

            @PathVariable
            UUID employeeId,

            @Valid
            @RequestBody
            GrantPermissionRequest request
    ) {

        PermissionResponseDTO response =

                permissionService
                        .grantPermissions(
                                employeeId,
                                request
                        );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<PermissionResponseDTO>
                                builder()

                        .status(true)

                        .responseCode(200)

                        .responseMessage(
                                "Permissions granted successfully"
                        )

                        .responseData(
                                response
                        )

                        .build()
        );
    }

    @PutMapping(
            "/employees/{employeeId}"
    )
    public ResponseEntity<
            AuthResponseDTO<
                    PermissionResponseDTO
                    >
            > updatePermissions(

            @PathVariable
            UUID employeeId,

            @Valid
            @RequestBody
            UpdatePermissionRequest request
    ) {

        PermissionResponseDTO response =

                permissionService
                        .updatePermissions(
                                employeeId,
                                request
                        );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<PermissionResponseDTO>
                                builder()

                        .status(true)

                        .responseCode(200)

                        .responseMessage(
                                "Permissions updated successfully"
                        )

                        .responseData(
                                response
                        )

                        .build()
        );
    }

    @GetMapping(
            "/employees/{employeeId}"
    )
    public ResponseEntity<
            AuthResponseDTO<
                    PermissionResponseDTO
                    >
            >
    getPermissions(

            @PathVariable
            UUID employeeId
    ) {

        PermissionResponseDTO response =

                permissionService
                        .getPermissions(
                                employeeId
                        );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<PermissionResponseDTO>
                                builder()

                        .status(true)

                        .responseCode(200)

                        .responseMessage(
                                "Permissions fetched successfully"
                        )

                        .responseData(
                                response
                        )

                        .build()
        );
    }

    @DeleteMapping(
            "/employees/{employeeId}"
    )
    public ResponseEntity<
            AuthResponseDTO<
                    Object
                    >
            >
    revokePermissions(

            @PathVariable
            UUID employeeId
    ) {

        permissionService
                .revokePermission(
                        employeeId
                );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .builder()

                        .status(true)

                        .responseCode(200)

                        .responseMessage(
                                "Permissions revoked successfully"
                        )

                        .responseData(
                                null
                        )

                        .build()
        );
    }
}

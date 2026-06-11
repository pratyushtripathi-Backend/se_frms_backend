package com.se_frms.roleAccess.controller;

import com.se_frms.roleAccess.dto.RoleAccessRequestDTO;
import com.se_frms.roleAccess.service.RoleAccessService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping(
        "/api/v1/role-access"
)

@RequiredArgsConstructor
public class RoleAccessController {

    private final RoleAccessService service;

    @PostMapping
    public ResponseEntity<?> create(

            @RequestBody
            RoleAccessRequestDTO request

    ) {

        return ResponseEntity.ok(

                service.create(
                        request
                )

        );

    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(

                service.getAll()

        );

    }

    @GetMapping(
            "/{roleId}"
    )

    public ResponseEntity<?> getByRole(

            @PathVariable
            Integer roleId

    ) {

        return ResponseEntity.ok(

                service.getByRole(
                        roleId
                )

        );

    }

    @PatchMapping(
            "/{roleId}/{accessId}/status"
    )

    public ResponseEntity<?> updateStatus(

            @PathVariable
            Integer roleId,

            @PathVariable
            Integer accessId,

            @RequestParam
            Boolean status

    ) {

        return ResponseEntity.ok(

                service.updateAccessStatus(

                        roleId,

                        accessId,

                        status

                )

        );

    }

}
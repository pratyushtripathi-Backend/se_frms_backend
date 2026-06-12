package com.se_frms.access.controller;

import com.se_frms.access.dto.AccessRequestDTO;
import com.se_frms.access.dto.AccessResponseDTO;
import com.se_frms.access.service.AccessService;
import com.se_frms.auth.dto.AuthResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/access")
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/access-name")
    public ResponseEntity<AuthResponseDTO<AccessResponseDTO>>
    create(
            @RequestBody
            AccessRequestDTO request
    ) {

        log.info("Create access request received");

        AccessResponseDTO response =
                accessService.create(
                        request
                );

        log.info("Access created successfully");

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        AuthResponseDTO
                                .<AccessResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage("Access created successfully")
                                .responseData(response)
                                .build()
                );
    }

    @GetMapping("/get-access-list")
    public ResponseEntity<AuthResponseDTO<List<AccessResponseDTO>>>
    getAll() {

        log.info("Fetch all access request received");

        List<AccessResponseDTO> response =
                accessService.getAll();

        log.info(
                "Access list fetched successfully, count={}",
                response.size()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<AccessResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Access list fetched successfully")
                        .responseData(response)
                        .build()
        );
    }

    @GetMapping("/get-specific-access/{id}")
    public ResponseEntity<AuthResponseDTO<AccessResponseDTO>>
    getById(
            @PathVariable
            Integer id
    ) {

        log.info("Fetch access request received, id={}", id);

        AccessResponseDTO response =
                accessService.getById(
                        id
                );

        log.info("Access fetched successfully, id={}", id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<AccessResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Access fetched successfully")
                        .responseData(response)
                        .build()
        );
    }

    @PutMapping("update-access/{id}")
    public ResponseEntity<AuthResponseDTO<AccessResponseDTO>>
    update(
            @PathVariable
            Integer id,

            @RequestBody
            AccessRequestDTO request
    ) {

        log.info("Update access request received, id={}", id);

        AccessResponseDTO response =
                accessService.update(
                        id,
                        request
                );

        log.info("Access updated successfully, id={}", id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<AccessResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Access updated successfully")
                        .responseData(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AuthResponseDTO<String>>
    updateStatus(
            @PathVariable
            Integer id,

            @RequestParam
            Boolean status
    ) {

        log.info(
                "Update access status request received, id={}, status={}",
                id,
                status
        );

        String response =
                accessService.updateStatus(
                        id,
                        status
                );

        log.info(
                "Access status updated successfully, id={}, status={}",
                id,
                status
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<String>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Access status updated successfully")
                        .responseData(response)
                        .build()
        );
    }

    @DeleteMapping("delete-acccess/{id}")
    public ResponseEntity<AuthResponseDTO<String>>
    delete(
            @PathVariable
            Integer id
    ) {

        log.info("Delete access request received, id={}", id);

        String response =
                accessService.delete(
                        id
                );

        log.info("Access deleted successfully, id={}", id);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<String>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Access deleted successfully")
                        .responseData(response)
                        .build()
        );
    }
}

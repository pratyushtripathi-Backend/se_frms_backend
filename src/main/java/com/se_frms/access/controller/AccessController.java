package com.se_frms.access.controller;

import com.se_frms.access.dto.AccessRequestDTO;
import com.se_frms.access.dto.AccessResponseDTO;
import com.se_frms.access.service.AccessService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/access")
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/access-name")
    public ResponseEntity<AccessResponseDTO>
    create(

            @RequestBody
            AccessRequestDTO request

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )

                .body(

                        accessService
                                .create(
                                        request
                                )

                );

    }

    @GetMapping("/get-access-list")
    public ResponseEntity<List<AccessResponseDTO>>
    getAll() {

        return ResponseEntity.ok(

                accessService
                        .getAll()

        );

    }

    @GetMapping("/get-specific-access/{id}")
    public ResponseEntity<AccessResponseDTO>
    getById(

            @PathVariable
            Integer id

    ) {

        return ResponseEntity.ok(

                accessService
                        .getById(
                                id
                        )

        );

    }

    @PutMapping("update-access/{id}")
    public ResponseEntity<AccessResponseDTO>
    update(

            @PathVariable
            Integer id,

            @RequestBody
            AccessRequestDTO request

    ) {

        return ResponseEntity.ok(

                accessService
                        .update(
                                id,
                                request
                        )

        );

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String>
    updateStatus(

            @PathVariable
            Integer id,

            @RequestParam
            Boolean status

    ) {

        return ResponseEntity.ok(

                accessService
                        .updateStatus(
                                id,
                                status
                        )

        );

    }

    @DeleteMapping("delete-acccess/{id}")
    public ResponseEntity<String>
    delete(

            @PathVariable
            Integer id

    ) {

        return ResponseEntity.ok(

                accessService
                        .delete(
                                id
                        )

        );

    }

}
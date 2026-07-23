package com.se_frms.userRole.controller;

import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.userRole.dto.UserRoleRequestDTO;
import com.se_frms.userRole.dto.UserRoleResponseDTO;
import com.se_frms.userRole.dto.UserRoleStatusRequestDTO;
import com.se_frms.userRole.service.UserRoleService;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import com.se_frms.common.dto.PagedResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO<UserRoleResponseDTO>>
    assignRole(
            @Valid
            @RequestBody
            UserRoleRequestDTO request
    ) {

        log.info("Assign role request received");

        UserRoleResponseDTO responseData =
                userRoleService.assignRole(request);

        log.info("Role assigned successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<UserRoleResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage("Role assigned successfully")
                                .responseData(responseData)
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<UserRoleResponseDTO>>>
    getAllUserRoles(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        log.info(
                "Fetch all user roles request received, page={}, size={}",
                page,
                size
        );

        Page<UserRoleResponseDTO> pageData =
                userRoleService.getAllUserRoles(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<UserRoleResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        log.info(
                "User roles fetched successfully, count={}",
                pageData.getNumberOfElements()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<PagedResponseDTO<UserRoleResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("User roles fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<AuthResponseDTO<List<UserRoleResponseDTO>>>
    getActiveUserRoles() {

        log.info("Fetch active user roles request received");

        List<UserRoleResponseDTO> responseData =
                userRoleService.getActiveUserRoles();

        log.info("Active user roles fetched successfully, count={}", responseData.size());

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<UserRoleResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("Active user roles fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AuthResponseDTO<List<UserRoleResponseDTO>>>
    getRolesByUser(
            @PathVariable
            Integer userId
    ) {

        log.info("Fetch roles by user request received, userId={}", userId);

        List<UserRoleResponseDTO> responseData =
                userRoleService.getRolesByUser(userId);

        log.info(
                "User roles fetched successfully, userId={}, count={}",
                userId,
                responseData.size()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<UserRoleResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("User roles fetched successfully")
                        .responseData(responseData)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AuthResponseDTO<UserRoleResponseDTO>>
    updateStatus(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            UserRoleStatusRequestDTO request
    ) {

        log.info(
                "Update user role request received, id={}, status={}",
                id,
                request.getStatus()
        );

        UserRoleResponseDTO responseData =
                userRoleService.updateStatus(
                        id,
                        request
                );

        log.info(
                "User role updated successfully, id={}, status={}",
                id,
                request.getStatus()
        );

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<UserRoleResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage("User role updated successfully")
                        .responseData(responseData)
                        .build()
        );
    }
}

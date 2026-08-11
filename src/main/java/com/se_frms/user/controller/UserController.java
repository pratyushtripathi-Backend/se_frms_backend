package com.se_frms.user.controller;



import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.auth.service.AuthService;

import com.se_frms.common.dto.PagedResponseDTO;
import com.se_frms.user.dto.UpdateUserRequest;
import com.se_frms.user.dto.UserResponseDTO;

import com.se_frms.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.se_frms.user.dto.UserStatusRequestDTO;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<UserResponseDTO>>>
    getAllUsers(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        Page<UserResponseDTO> pageData =
                userService.getAllUsers(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<UserResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        AuthResponseDTO<PagedResponseDTO<UserResponseDTO>> response =
                AuthResponseDTO
                        .<PagedResponseDTO<UserResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Users fetched successfully"
                        )
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/non-admin")
    public ResponseEntity<AuthResponseDTO<PagedResponseDTO<UserResponseDTO>>>
    getAllNonAdminUsers(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam
            Map<String, String> filters
    ) {

        Page<UserResponseDTO> pageData =
                userService.getAllNonAdminUsers(
                        page,
                        size,
                        filters
                );

        PagedResponseDTO<UserResponseDTO> responseData =
                PagedResponseDTO.from(pageData);

        AuthResponseDTO<PagedResponseDTO<UserResponseDTO>> response =
                AuthResponseDTO
                        .<PagedResponseDTO<UserResponseDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Non-admin users fetched successfully"
                        )
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<UserResponseDTO>>
    getUserById(
            @PathVariable Integer id
    ) {

        UserResponseDTO responseData =
                userService.getUserById(id);

        AuthResponseDTO<UserResponseDTO> response =
                AuthResponseDTO
                        .<UserResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "User fetched successfully"
                        )
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthResponseDTO<UserResponseDTO>>
    updateUser(
            @PathVariable Integer id,

            @Valid
            @RequestBody
            UpdateUserRequest request,

            jakarta.servlet.http.HttpServletRequest httpRequest
    ) {

        UserResponseDTO responseData =
                userService.updateUser(
                        id,
                        request
                );

        if (Boolean.TRUE.equals(responseData.getLogoutRequired())) {
            authService.logout(
                    extractBearerToken(httpRequest)
            );
        }

        AuthResponseDTO<UserResponseDTO> response =
                AuthResponseDTO
                        .<UserResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                Boolean.TRUE.equals(responseData.getLogoutRequired())
                                        ? "User updated successfully. Please login again."
                                        : "User updated successfully"
                        )
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AuthResponseDTO<UserResponseDTO>>
    updateUserStatus(
            @PathVariable
            Integer id,

            @Valid
            @RequestBody
            UserStatusRequestDTO request
    ) {

        UserResponseDTO responseData =
                userService.updateUserStatus(
                        id,
                        request
                );

        AuthResponseDTO<UserResponseDTO> response =
                AuthResponseDTO
                        .<UserResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "User status updated successfully"
                        )
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }

    private String extractBearerToken(
            jakarta.servlet.http.HttpServletRequest request
    ) {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token not found");
        }

        return authHeader.substring(7);
    }
}

package com.se_frms.user.controller;



import com.se_frms.auth.dto.AuthResponseDTO;

import com.se_frms.user.dto.UserResponseDTO;

import com.se_frms.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessUser(authentication, #id, T(com.se_frms.common.security.Permission).USERS_READ.value())")
    public ResponseEntity<AuthResponseDTO<UserResponseDTO>>
    getUserById(
            @PathVariable UUID id
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
}

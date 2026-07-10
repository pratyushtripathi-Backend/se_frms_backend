package com.se_frms.user.controller;



import com.se_frms.auth.dto.AuthResponseDTO;

import com.se_frms.user.dto.UpdateUserRequest;
import com.se_frms.user.dto.UserResponseDTO;

import com.se_frms.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
            UpdateUserRequest request
    ) {

        UserResponseDTO responseData =
                userService.updateUser(
                        id,
                        request
                );

        AuthResponseDTO<UserResponseDTO> response =
                AuthResponseDTO
                        .<UserResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "User updated successfully"
                        )
                        .responseData(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }
}

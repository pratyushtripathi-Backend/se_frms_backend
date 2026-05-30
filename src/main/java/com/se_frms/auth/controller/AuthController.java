package com.se_frms.auth.controller;


import com.se_frms.auth.dto.*;

import com.se_frms.auth.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO<RegistrationResponseDTO>>
    registerUser(
            @Valid
            @RequestBody
            UserRegistrationRequest request
    ) {

        RegistrationResponseDTO responseData =
                authService.registerUser(request);

        AuthResponseDTO<RegistrationResponseDTO> response =
                AuthResponseDTO
                        .<RegistrationResponseDTO>builder()
                        .status(true)
                        .responseCode(201)
                        .responseMessage("User registered successfully")
                        .responseData(responseData)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}


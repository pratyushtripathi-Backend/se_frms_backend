package com.se_frms.admin.Controller;



import com.se_frms.admin.dto.CreateEmployeeRequest;
import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @PostMapping("/employees")
    public ResponseEntity<
            AuthResponseDTO<RegistrationResponseDTO>>
    createEmployee(
            @Valid
            @RequestBody
            CreateEmployeeRequest request
    ) {

        RegistrationResponseDTO responseData =
                authService.createEmployee(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AuthResponseDTO
                                .<RegistrationResponseDTO>builder()
                                .status(true)
                                .responseCode(201)
                                .responseMessage(
                                        "Employee created successfully"
                                )
                                .responseData(responseData)
                                .build()
                );
    }
}

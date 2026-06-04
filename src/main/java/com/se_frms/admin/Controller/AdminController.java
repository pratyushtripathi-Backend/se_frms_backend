package com.se_frms.admin.Controller;



import com.se_frms.admin.dto.*;
import com.se_frms.admin.service.AdminService;
import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.service.AuthService;

import com.se_frms.user.dto.UserResponseDTO;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/employees")
    public ResponseEntity<
            AuthResponseDTO<RegistrationResponseDTO>>
    createEmployee(
            @Valid
            @RequestBody
            CreateEmployeeRequest request
    ) {

        RegistrationResponseDTO responseData =
                adminService.createEmployee(request);

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

    @GetMapping("/employees")
    public ResponseEntity<
            AuthResponseDTO<List<EmployeeSummaryDTO>>>
    getAllEmployees() {

        List<EmployeeSummaryDTO> responseData =
                adminService.getAllEmployees();

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<List<EmployeeSummaryDTO>>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Employees fetched successfully"
                        )
                        .responseData(responseData)
                        .build()
        );
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<
            AuthResponseDTO<EmployeeResponseDTO>>
    getEmployeeById(
            @PathVariable
            UUID employeeId
    ) {

        EmployeeResponseDTO responseData =
                adminService.getEmployeeById(employeeId);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<EmployeeResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Employee fetched successfully"
                        )
                        .responseData(responseData)
                        .build()
        );
    }

    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<AuthResponseDTO<EmployeeResponseDTO>>
    updateEmployee(

            @PathVariable
            UUID employeeId,

            @Valid
            @RequestBody
            UpdateEmployeeRequest request
    ) {

        EmployeeResponseDTO response =
                adminService.updateEmployee(
                        employeeId,
                        request
                );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<EmployeeResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Employee updated successfully"
                        )
                        .responseData(response)
                        .build()
        );
    }

    @PatchMapping("/employees/{employeeId}")
    public ResponseEntity<AuthResponseDTO<EmployeeResponseDTO>>
    patchEmployee(

            @PathVariable
            UUID employeeId,

            @RequestBody
            UpdateEmployeePatchRequest request
    ) {

        EmployeeResponseDTO response =
                adminService.patchEmployee(
                        employeeId,
                        request
                );

        return ResponseEntity.ok(

                AuthResponseDTO
                        .<EmployeeResponseDTO>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Employee updated successfully"
                        )
                        .responseData(response)
                        .build()
        );
    }

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<
            AuthResponseDTO<Void>>
    deleteEmployee(
            @PathVariable
            UUID employeeId
    ) {

        adminService.deleteEmployee(employeeId);

        return ResponseEntity.ok(
                AuthResponseDTO
                        .<Void>builder()
                        .status(true)
                        .responseCode(200)
                        .responseMessage(
                                "Employee deleted successfully"
                        )
                        .responseData(null)
                        .build()
        );
    }
}

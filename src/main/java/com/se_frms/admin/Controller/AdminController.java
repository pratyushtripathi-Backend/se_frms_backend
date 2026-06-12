package com.se_frms.admin.Controller;



import com.se_frms.admin.dto.*;
import com.se_frms.admin.service.AdminService;
import com.se_frms.auth.dto.AuthResponseDTO;
import com.se_frms.auth.dto.RegistrationResponseDTO;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
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
        log.info("Create employee request received");
        RegistrationResponseDTO responseData =
                adminService.createEmployee(request);
        log.info("Employee created successfully");

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
        log.info("Fetch all employees request received");
        List<EmployeeSummaryDTO> responseData =
                adminService.getAllEmployees();
        log.info("Employees fetched successfully, count={}", responseData.size());
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
            Integer employeeId
    ) {
        log.info("Fetch employee request received, employeeId={}", employeeId);
        EmployeeResponseDTO responseData =
                adminService.getEmployeeById(employeeId);
        log.info("Employee fetched successfully, employeeId={}", employeeId);
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
            Integer employeeId,

            @Valid
            @RequestBody
            UpdateEmployeeRequest request
    ) {
        log.info("Update employee request received, employeeId={}", employeeId);
        EmployeeResponseDTO response =
                adminService.updateEmployee(
                        employeeId,
                        request
                );
        log.info("Employee updated successfully, employeeId={}", employeeId);
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
            Integer employeeId,

            @RequestBody
            UpdateEmployeePatchRequest request
    ) {
        log.info("Patch employee request received, employeeId={}", employeeId);
        EmployeeResponseDTO response =
                adminService.patchEmployee(
                        employeeId,
                        request
                );
        log.info("Employee patched successfully, employeeId={}", employeeId);
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
            Integer employeeId
    ) {
        log.info("Delete employee request received, employeeId={}", employeeId);
        adminService.deleteEmployee(employeeId);
        log.info("Employee deleted successfully, employeeId={}", employeeId);
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

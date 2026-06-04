package com.se_frms.admin.service;

import com.se_frms.admin.dto.*;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.user.dto.UserResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    RegistrationResponseDTO createEmployee(
            CreateEmployeeRequest request
    );

    EmployeeResponseDTO getEmployeeById(
            UUID employeeId
    );

    List<EmployeeSummaryDTO> getAllEmployees();

    void deleteEmployee(
            UUID employeeId
    );

    EmployeeResponseDTO updateEmployee(
            UUID employeeId,
            UpdateEmployeeRequest request
    );

    EmployeeResponseDTO patchEmployee(
            UUID employeeId,
            UpdateEmployeePatchRequest request
    );
}

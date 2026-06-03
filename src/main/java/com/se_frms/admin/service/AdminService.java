package com.se_frms.admin.service;

import com.se_frms.admin.dto.CreateEmployeeRequest;
import com.se_frms.admin.dto.EmployeeResponseDTO;
import com.se_frms.admin.dto.EmployeeSummaryDTO;
import com.se_frms.auth.dto.RegistrationResponseDTO;
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
}

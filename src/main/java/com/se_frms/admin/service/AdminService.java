package com.se_frms.admin.service;

import com.se_frms.admin.dto.CreateEmployeeRequest;
import com.se_frms.admin.dto.EmployeeResponseDTO;
import com.se_frms.admin.dto.EmployeeSummaryDTO;
import com.se_frms.admin.dto.UpdateEmployeePatchRequest;
import com.se_frms.admin.dto.UpdateEmployeeRequest;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AdminService {

    RegistrationResponseDTO createEmployee(
            CreateEmployeeRequest request
    );

    EmployeeResponseDTO getEmployeeById(
            Integer employeeId
    );

    List<EmployeeSummaryDTO> getAllEmployees();

    Page<EmployeeSummaryDTO> getAllEmployees(
            int page,
            int size
    );

    Page<EmployeeSummaryDTO> getAllEmployees(
            int page,
            int size,
            Map<String, String> filters
    );

    void deleteEmployee(
            Integer employeeId
    );

    EmployeeResponseDTO updateEmployee(
            Integer employeeId,
            UpdateEmployeeRequest request
    );

    EmployeeResponseDTO patchEmployee(
            Integer employeeId,
            UpdateEmployeePatchRequest request
    );

}

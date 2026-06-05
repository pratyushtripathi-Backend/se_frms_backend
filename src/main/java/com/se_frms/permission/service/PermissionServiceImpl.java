package com.se_frms.permission.service;



import com.se_frms.auth.exception.InvalidRequestException;

import com.se_frms.permission.dto.*;
import com.se_frms.permission.enums.Permission;
import com.se_frms.permission.model.UserPermission;
import com.se_frms.permission.repository.UserPermissionRepository;

import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl
        implements PermissionService {

    private final UserRepository userRepository;

    private final UserPermissionRepository
            userPermissionRepository;

    @Override
    public PermissionResponseDTO grantPermissions(
            UUID employeeId,
            GrantPermissionRequest request
    ) {

        User employee =
                getEmployee(employeeId);

        for (
                Permission permission :
                request.getPermissions()
        ) {

            if (
                    userPermissionRepository
                            .existsByUserIdAndPermission(
                                    employeeId,
                                    permission
                            )
            ) {
                continue;
            }

            userPermissionRepository.save(

                    UserPermission
                            .builder()
                            .user(employee)
                            .permission(
                                    permission
                            )
                            .build()
            );
        }

        return buildResponse(
                employee
        );
    }

    @Override
    public PermissionResponseDTO updatePermissions(
            UUID employeeId,
            UpdatePermissionRequest request
    ) {

        User employee =
                getEmployee(
                        employeeId
                );

        userPermissionRepository
                .deleteByUserId(
                        employeeId
                );

        for (
                Permission permission :
                request.getPermissions()
        ) {

            userPermissionRepository.save(

                    UserPermission
                            .builder()
                            .user(
                                    employee
                            )
                            .permission(
                                    permission
                            )
                            .build()
            );
        }

        return buildResponse(
                employee
        );
    }
    @Override
    public PermissionResponseDTO getPermissions(
            UUID employeeId
    ) {

        return buildResponse(
                getEmployee(
                        employeeId
                )
        );
    }

    @Override
    public void revokePermission(
            UUID employeeId
    ) {

        getEmployee(
                employeeId
        );

        userPermissionRepository
                .deleteByUserId(
                        employeeId
                );
    }

    private User getEmployee(
            UUID employeeId
    ) {

        User employee =
                userRepository
                        .findById(
                                employeeId
                        )
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                "Employee not found"
                                        )
                        );

        if (
                employee.getRole()
                        != Role.EMPLOYEE
        ) {

            throw new InvalidRequestException(
                    "Permission can only be assigned to employee"
            );
        }

        return employee;
    }

    private PermissionResponseDTO
    buildResponse(
            User employee
    ) {

        List<Permission>
                permissions =

                userPermissionRepository
                        .findByUserId(
                                employee.getId()
                        )
                        .stream()
                        .map(
                                UserPermission::getPermission
                        )
                        .toList();

        return PermissionResponseDTO
                .builder()
                .userId(
                        employee.getId()
                )
                .employeeName(
                        employee.getFirstName()
                                + " "
                                + employee.getLastName()
                )
                .permissions(
                        permissions
                )
                .build();
    }
}
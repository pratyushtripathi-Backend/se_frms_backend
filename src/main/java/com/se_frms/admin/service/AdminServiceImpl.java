package com.se_frms.admin.service;

import com.se_frms.admin.dto.*;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.auth.util.PasswordGeneratorUtil;
import com.se_frms.mail.service.MailService;
import com.se_frms.user.dto.UserResponseDTO;
import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    public RegistrationResponseDTO createEmployee(
            CreateEmployeeRequest request
    ) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String phoneNumber = request.getPhoneNumber()
                .trim();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                    "Email already registered"
            );
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicatePhoneException(
                    "Phone number already registered"
            );
        }

        String generatedPassword =
                PasswordGeneratorUtil.generateSecurePassword();

        String encryptedPassword =
                passwordEncoder.encode(generatedPassword);

        User employee = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(email)
                .phoneNumber(phoneNumber)
                .passwordHash(encryptedPassword)
                .role(Role.EMPLOYEE)
                .build();

        User savedEmployee =
                userRepository.save(employee);

        mailService.sendLoginCredentials(
                savedEmployee.getEmail(),
                savedEmployee.getFirstName(),
                generatedPassword
        );

        return RegistrationResponseDTO.builder()
                .userId(savedEmployee.getId())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeResponseDTO getEmployeeById(
            UUID employeeId
    ) {

        User employee = userRepository.findById(employeeId)
                .orElseThrow(
                        () -> new InvalidRequestException(
                                "Employee not found"
                        )
                );

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new InvalidRequestException(
                    "User is not an employee"
            );
        }

        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .role(employee.getRole().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSummaryDTO> getAllEmployees() {

        return userRepository.findByRole(Role.EMPLOYEE)
                .stream()
                .map(employee ->
                        EmployeeSummaryDTO.builder()
                                .id(employee.getId())
                                .firstName(employee.getFirstName())
                                .lastName(employee.getLastName())
                                .email(employee.getEmail())
                                .build()
                )
                .toList();
    }

    @Override
    public void deleteEmployee(
            UUID employeeId
    ) {

        User employee = userRepository.findById(employeeId)
                .orElseThrow(
                        () -> new InvalidRequestException(
                                "Employee not found"
                        )
                );

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new InvalidRequestException(
                    "Only employees can be deleted"
            );
        }

        userRepository.delete(employee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(
            UUID employeeId,
            UpdateEmployeeRequest request
    ) {

        User user =
                userRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                "Employee not found"
                                        )
                        );

        if (
                user.getRole()
                        != Role.EMPLOYEE
        ) {

            throw new InvalidRequestException(
                    "Only employee can be updated"
            );
        }

        if (
                !user.getEmail()
                        .equals(
                                request.getEmail()
                        )
                        &&
                        userRepository.existsByEmail(
                                request.getEmail()
                        )
        ) {

            throw new DuplicateEmailException(
                    "Email already exists"
            );
        }

        if (
                !user.getPhoneNumber()
                        .equals(
                                request.getPhoneNumber()
                        )
                        &&
                        userRepository.existsByPhoneNumber(
                                request.getPhoneNumber()
                        )
        ) {

            throw new DuplicatePhoneException(
                    "Phone already exists"
            );
        }

        user.setFirstName(
                request.getFirstName()
        );

        user.setLastName(
                request.getLastName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPhoneNumber(
                request.getPhoneNumber()
        );

        user.setIsActive(
                request.getIsActive()
        );

        userRepository.save(
                user
        );

         return EmployeeResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public EmployeeResponseDTO patchEmployee(
            UUID employeeId,
            UpdateEmployeePatchRequest request
    ) {

        User user =
                userRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                "Employee not found"
                                        )
                        );

        if (user.getRole() != Role.EMPLOYEE) {

            throw new InvalidRequestException(
                    "Only employee can be updated"
            );
        }

        if (
                request.getEmail() != null
                        &&
                        !request.getEmail()
                                .equals(user.getEmail())
                        &&
                        userRepository.existsByEmail(
                                request.getEmail()
                        )
        ) {

            throw new DuplicateEmailException(
                    "Email already exists"
            );
        }

        if (
                request.getPhoneNumber() != null
                        &&
                        !request.getPhoneNumber()
                                .equals(user.getPhoneNumber())
                        &&
                        userRepository.existsByPhoneNumber(
                                request.getPhoneNumber()
                        )
        ) {

            throw new DuplicatePhoneException(
                    "Phone already exists"
            );
        }

        if (request.getFirstName() != null)
            user.setFirstName(
                    request.getFirstName()
            );

        if (request.getLastName() != null)
            user.setLastName(
                    request.getLastName()
            );

        if (request.getEmail() != null)
            user.setEmail(
                    request.getEmail()
            );

        if (request.getPhoneNumber() != null)
            user.setPhoneNumber(
                    request.getPhoneNumber()
            );

        if (request.getIsActive() != null)
            user.setIsActive(
                    request.getIsActive()
            );

        userRepository.save(user);

        return EmployeeResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
package com.se_frms.admin.service;

import com.se_frms.admin.dto.*;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.auth.util.PasswordGeneratorUtil;
import com.se_frms.common.security.XssUtil;
import com.se_frms.mail.service.MailService;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.lang.Integer;


@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl
        implements AdminService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    @Override
    public RegistrationResponseDTO createEmployee(
            CreateEmployeeRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        String phone =
                request.getPhoneNumber()
                        .trim();

        if (
                userRepository.existsByEmail(
                        email
                )
        ) {

            throw new DuplicateEmailException(
                    "Email already registered"
            );

        }

        if (
                userRepository.existsByPhoneNumber(
                        phone
                )
        ) {

            throw new DuplicatePhoneException(
                    "Phone already registered"
            );

        }

        String generatedPassword =
                PasswordGeneratorUtil
                        .generateSecurePassword();

        String encryptedPassword =
                passwordEncoder.encode(
                        generatedPassword
                );

        User employee = User.builder()

                .firstName(
                        XssUtil.clean(
                                request.getFirstName().trim()
                        )
                )

                .lastName(
                        XssUtil.clean(
                                request.getLastName().trim()
                        )
                )

                .email(
                        XssUtil.clean(
                                email
                        )
                )

                .phoneNumber(
                        XssUtil.clean(
                                phone
                        )
                )

                .passwordHash(
                        encryptedPassword
                )

                .userType(
                        request.getRole()
                )

                .status(
                        true
                )

                .build();
        User saved =
                userRepository.save(
                        employee
                );

        mailService.sendLoginCredentials(

                saved.getEmail(),

                saved.getFirstName(),

                generatedPassword

        );

        return RegistrationResponseDTO
                .builder()

                .userId(
                        saved.getId()
                )

                .firstName(
                        saved.getFirstName()
                )

                .lastName(
                        saved.getLastName()
                )

                .email(
                        saved.getEmail()
                )

                .phoneNumber(
                        saved.getPhoneNumber()
                )

                .role(
                        saved.getUserType()
                )

                .status(
                        saved.getStatus()
                )

                .createdDate(
                        saved.getCreatedDate()
                )

                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(
            Integer employeeId
    ) {

        User user =

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

                !"EMPLOYEE".equals(
                        user.getUserType()
                )

        ) {

            throw new InvalidRequestException(
                    "User is not employee"
            );

        }

        return map(
                user
        );

    }

    @Override
    public List<EmployeeSummaryDTO> getAllEmployees() {

        return userRepository
                .findByUserType(
                        "EMPlOYEE"
                )

                .stream()

                .map(

                        user ->

                                EmployeeSummaryDTO

                                        .builder()

                                        .id(
                                                user.getId()
                                        )

                                        .firstName(
                                                user.getFirstName()
                                        )

                                        .lastName(
                                                user.getLastName()
                                        )

                                        .email(
                                                user.getEmail()
                                        )

                                        .build()

                )

                .toList();

    }

    @Override
    public void deleteEmployee(
            Integer employeeId
    ) {

        User user =

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

                !"EMP".equals(
                        user.getUserType()
                )

        ) {

            throw new InvalidRequestException(
                    "Only employee can be deleted"
            );

        }

        user.setStatus(
                false
        );

        userRepository.save(
                user
        );

    }

    @Override
    public EmployeeResponseDTO updateEmployee(
            Integer employeeId,
            UpdateEmployeeRequest request
    ) {

        User user =

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

        user.setStatus(
                request.getIsActive()
        );

        return map(

                userRepository.save(
                        user
                )

        );

    }

    @Override
    public EmployeeResponseDTO patchEmployee(
            Integer employeeId,
            UpdateEmployeePatchRequest request
    ) {

        User user =

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
            user.setStatus(
                    request.getIsActive()
            );

        return map(

                userRepository.save(
                        user
                )

        );

    }

    private EmployeeResponseDTO map(
            User user
    ) {

        return EmployeeResponseDTO
                .builder()

                .id(
                        user.getId()
                )

                .firstName(
                        user.getFirstName()
                )

                .lastName(
                        user.getLastName()
                )

                .email(
                        user.getEmail()
                )

                .phoneNumber(
                        user.getPhoneNumber()
                )

                .role(
                        user.getUserType()
                )

                .build();

    }

}

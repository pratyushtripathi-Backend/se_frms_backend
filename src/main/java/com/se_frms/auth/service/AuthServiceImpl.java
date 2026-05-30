package com.se_frms.auth.service;


import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.auth.dto.RegistrationResponseDTO;

import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRoleException;

import com.se_frms.auth.service.AuthService;

import com.se_frms.auth.util.PasswordGeneratorUtil;

import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public RegistrationResponseDTO registerUser(
            UserRegistrationRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        String phoneNumber =
                request.getPhoneNumber()
                        .trim();

        validateDuplicateEmail(email);

        validateDuplicatePhone(phoneNumber);

        Role role = validateAndAssignRole();

        String generatedPassword =
                PasswordGeneratorUtil
                        .generateSecurePassword();

        String encryptedPassword =
                passwordEncoder.encode(
                        generatedPassword
                );

        User user = User.builder()

                .firstName(
                        request.getFirstName().trim()
                )

                .lastName(
                        request.getLastName().trim()
                )

                .email(email)

                .phoneNumber(phoneNumber)

                .passwordHash(
                        encryptedPassword
                )

                .role(role)

                .build();

        User savedUser =
                userRepository.save(user);

        return RegistrationResponseDTO
                .builder()
                .userId(savedUser.getId())
                .build();
    }

    private void validateDuplicateEmail(
            String email
    ) {

        if (userRepository.existsByEmail(email)) {

            throw new DuplicateEmailException(
                    "Email already registered"
            );
        }
    }

    private void validateDuplicatePhone(
            String phoneNumber
    ) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {

            throw new DuplicatePhoneException(
                    "Phone number already registered"
            );
        }
    }

    private Role validateAndAssignRole() {

        Role role = Role.EMPLOYEE;

        if (role == Role.ADMIN) {

            throw new InvalidRoleException(
                    "Public ADMIN registration is not allowed"
            );
        }

        return role;
    }
}



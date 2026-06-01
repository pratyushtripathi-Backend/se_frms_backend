package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginRequestDTO;
import com.se_frms.auth.dto.LoginResponseDTO;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.auth.exception.AccountBlockedException;
import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRoleException;
import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.se_frms.user.exception.InvalidCredentialsException;
import com.se_frms.common.security.JwtUtil;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

        String encryptedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(email)
                .phoneNumber(phoneNumber)
                .passwordHash(encryptedPassword)
                .role(role)
                .build();

        User savedUser =
                userRepository.save(user);

        return RegistrationResponseDTO
                .builder()
                .userId(savedUser.getId())
                .build();
    }

//    @Override
//    public LoginResponseDTO login(
//            LoginRequestDTO request
//    ) {
//        return null;
//    }


    @Override
    public LoginResponseDTO login(
            LoginRequestDTO request
    ) {

        User user =
                userRepository
                        .findByEmail(
                                request.getEmail()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new InvalidCredentialsException(
                                        "Invalid email or password"
                                )
                        );

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new AccountBlockedException("Your account is blocked");
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatches) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtUtil.generateToken(
                        user
                );

        return LoginResponseDTO
                .builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .tokenType("Bearer")
                .token(token)
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
                    "Public privileged registration is not allowed"
            );
        }

        return role;
    }
}

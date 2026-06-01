package com.se_frms.auth.service;

import com.se_frms.auth.dto.LoginRequestDTO;
import com.se_frms.auth.dto.LoginResponseDTO;

import com.se_frms.auth.dto.ResetPasswordRequest;
import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRoleException;

import com.se_frms.auth.exception.*;

import com.se_frms.auth.service.AuthService;

import com.se_frms.auth.util.PasswordGeneratorUtil;

import com.se_frms.mail.service.MailService;
import com.se_frms.passwordreset.model.PasswordResetToken;
import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;

import com.se_frms.auth.dto.ForgotPasswordRequest;
import com.se_frms.passwordreset.repository.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.se_frms.user.exception.InvalidCredentialsException;
import com.se_frms.common.security.JwtUtil;
import com.se_frms.auth.dto.SendOtpRequestDTO;
import com.se_frms.auth.dto.VerifyOtpRequestDTO;
import com.se_frms.auth.model.EmailOtp;
import com.se_frms.auth.repository.EmailOtpRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Random;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailOtpRepository emailOtpRepository;

    private final JavaMailSender mailSender;

    private final MailService mailService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

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
                PasswordGeneratorUtil.generateSecurePassword();

        String encryptedPassword =
                passwordEncoder.encode(generatedPassword);

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
        mailService.sendLoginCredentials(
                savedUser.getEmail(),
                savedUser.getFirstName(),
                generatedPassword
        );

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

    @Override
    public void forgotPassword(
            ForgotPasswordRequest request
    ) {

        User user =
                userRepository.findByEmail(
                        request.getEmail()
                ).orElseThrow(
                        () -> new InvalidRequestException(
                                "User not found"
                        )
                );

        String token =
                UUID.randomUUID().toString();

        String resetLink =
                "http://localhost:3000/reset-password?token="
                        + token;

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .used(false)
                        .expiryTime(
                                LocalDateTime.now()
                                        .plusMinutes(15)
                        )
                        .build();

        passwordResetTokenRepository.save(
                resetToken
        );

        mailService.sendPasswordResetMail(
                user.getEmail(),
                user.getFirstName(),
                resetLink
        );
    }

    @Override
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        if (request.getNewPassword() == null
                || request.getNewPassword().isBlank()) {

            throw new InvalidRequestException(
                    "New password is required"
            );
        }

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(
                                request.getToken()
                        )
                        .orElseThrow(
                                () -> new InvalidTokenException(
                                        "Invalid token"
                                )
                        );

        if (resetToken.isUsed()) {

            throw new InvalidTokenException(
                    "Token already used"
            );
        }

        if (resetToken.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new TokenExpiredException(
                    "Token expired"
            );
        }

        User user =
                resetToken.getUser();

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(
                user
        );

        resetToken.setUsed(true);

        passwordResetTokenRepository.save(
                resetToken
        );
    }

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

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return LoginResponseDTO
                .builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .build();
    }


//    @Override
//    public void sendOtp(
//            SendOtpRequestDTO request
//    ) {
//
//        User user =
//                userRepository
//                        .findByEmail(
//                                request.getEmail()
//                                        .trim()
//                                        .toLowerCase()
//                        )
//                        .orElseThrow(
//                                () -> new InvalidCredentialsException(
//                                        "Email not registered"
//                                )
//                        );
//
//        String otp =
//                String.valueOf(
//                        100000 +
//                                new Random().nextInt(900000)
//                );
//
//        EmailOtp emailOtp =
//                EmailOtp.builder()
//                        .email(user.getEmail())
//                        .otp(otp)
//                        .expiryTime(
//                                LocalDateTime.now()
//                                        .plusMinutes(5)
//                        )
//                        .verified(false)
//                        .build();
//
//        emailOtpRepository.save(emailOtp);
//
//        SimpleMailMessage message =
//                new SimpleMailMessage();
//
//        message.setTo(
//                user.getEmail()
//        );
//
//        message.setSubject(
//                "FRMS Login OTP"
//        );
//
//        message.setText(
//                "Your OTP is : " + otp
//        );
//
//        mailSender.send(message);
//    }

    @Override
    public void sendOtp(
            SendOtpRequestDTO request
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
                                        "Email not registered"
                                )
                        );

        String otp =
                String.valueOf(
                        100000 +
                                new Random().nextInt(900000)
                );

        EmailOtp emailOtp =
                EmailOtp.builder()
                        .email(user.getEmail())
                        .otp(otp)
                        .expiryTime(
                                LocalDateTime.now()
                                        .plusMinutes(5)
                        )
                        .verified(false)
                        .build();

        emailOtpRepository.save(emailOtp);

        System.out.println("OTP SAVED IN DB");

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(user.getEmail());
        message.setSubject("FRMS Login OTP");
        message.setText("Your OTP is : " + otp);

        System.out.println("BEFORE MAIL SEND");

        try {
            mailSender.send(message);
            System.out.println("MAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            System.out.println("MAIL ERROR");
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public LoginResponseDTO verifyOtp(
            VerifyOtpRequestDTO request
    ) {

        EmailOtp emailOtp =
                emailOtpRepository
                        .findTopByEmailOrderByIdDesc(
                                request.getEmail()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "OTP not found"
                                )
                        );

        if (
                emailOtp.getExpiryTime()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        if (
                !emailOtp.getOtp()
                        .equals(
                                request.getOtp()
                        )
        ) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        User user =
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        emailOtp.setVerified(true);

        emailOtpRepository.save(
                emailOtp
        );

        return LoginResponseDTO
                .builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .build();
    }
}
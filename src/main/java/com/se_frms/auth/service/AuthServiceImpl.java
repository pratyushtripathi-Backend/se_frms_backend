package com.se_frms.auth.service;

import com.se_frms.auth.dto.*;

import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;

import com.se_frms.auth.exception.*;
import com.se_frms.auth.model.BlacklistedToken;
import com.se_frms.auth.model.EmailOtp;
import com.se_frms.auth.repository.BlacklistedTokenRepository;
import com.se_frms.auth.repository.EmailOtpRepository;
import com.se_frms.auth.util.PasswordGeneratorUtil;
import com.se_frms.common.security.JwtUtil;
import com.se_frms.common.security.XssUtil;
import com.se_frms.mail.service.MailService;
import com.se_frms.passwordreset.model.PasswordResetToken;
import com.se_frms.passwordreset.repository.PasswordResetTokenRepository;
import com.se_frms.user.enums.Role;
import com.se_frms.user.exception.InvalidCredentialsException;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.lang.Integer;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailOtpRepository emailOtpRepository;
    private final SessionStoreService sessionStoreService;
    private final MailService mailService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final LoginHistoryService loginHistoryService;
    private final LoginAttemptService loginAttemptService;

    @Override
    public RegistrationResponseDTO registerUser(UserRegistrationRequest request) {

        log.info("Register user service started");

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String phoneNumber = request.getPhoneNumber().trim();

        validateDuplicateEmail(email);
        validateDuplicatePhone(phoneNumber);

        Role role = validateAndAssignRole();

        String generatedPassword = PasswordGeneratorUtil.generateSecurePassword();
        String encryptedPassword = passwordEncoder.encode(generatedPassword);

        User user = User.builder()
                .firstName(XssUtil.clean(request.getFirstName().trim()))
                .lastName(XssUtil.clean(request.getLastName().trim()))
                .email(XssUtil.clean(email))
                .phoneNumber(XssUtil.clean(phoneNumber))
                .passwordHash(encryptedPassword)
                .userType("EMPLOYEE")
                .build();

        User savedUser = userRepository.save(user);

        log.info("User saved successfully, userId={}, role={}", savedUser.getId(), savedUser.getUserType());

        mailService.sendLoginCredentials(
                savedUser.getEmail(),
                savedUser.getFirstName(),
                generatedPassword
        );

        log.info("Login credentials mail triggered, userId={}", savedUser.getId());

        return RegistrationResponseDTO
                .builder()
                .userId(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .role(savedUser.getUserType())
                .status(savedUser.getStatus())
                .createdDate(savedUser.getCreatedDate())
                .build();
    }

    private void validateDuplicateEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed because email already exists");
            throw new DuplicateEmailException("Email already registered");
        }
    }

    private void validateDuplicatePhone(String phoneNumber) {

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            log.warn("Registration failed because phone number already exists");
            throw new DuplicatePhoneException("Phone number already registered");
        }
    }

    private Role validateAndAssignRole() {

        Role role = Role.EMPLOYEE;

        if (role == Role.ADMIN) {
            log.warn("Public ADMIN registration blocked");
            throw new InvalidRoleException("Public ADMIN registration is not allowed");
        }

        return role;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        log.info("Forgot password service started");

        String token =
                String.valueOf(Math.random());
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Forgot password failed because user was not found");
                    return new InvalidRequestException("User not found");
                });

      //  String token = UUID.randomUUID().toString();

        String resetLink =
                "http://localhost:3000/reset-password?token=" + token;

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .used(false)
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .build();

        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset token created, userId={}", user.getId());

        mailService.sendPasswordResetMail(
                user.getEmail(),
                user.getFirstName(),
                resetLink
        );

        log.info("Password reset mail triggered, userId={}", user.getId());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        log.info("Reset password service started");

        if (request.getNewPassword() == null
                || request.getNewPassword().isBlank()) {

            log.warn("Reset password failed because new password was blank");
            throw new InvalidRequestException("New password is required");
        }

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElseThrow(() -> {
                            log.warn("Reset password failed because token was invalid");
                            return new InvalidTokenException("Invalid token");
                        });

        if (resetToken.isUsed()) {
            log.warn("Reset password failed because token was already used");
            throw new InvalidTokenException("Token already used");
        }

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            log.warn("Reset password failed because token was expired");
            throw new TokenExpiredException("Token expired");
        }

        User user = resetToken.getUser();

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successfully, userId={}", user.getId());
    }

    @Override
    public void changePassword(
            ChangePasswordRequest request
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new InvalidRequestException(
                                        "User not found"
                                )
                        );

        if (
                !passwordEncoder.matches(
                        request.getOldPassword(),
                        user.getPasswordHash()
                )
        ) {

            throw new InvalidRequestException(
                    "Old password is incorrect"
            );
        }

        if (
                passwordEncoder.matches(
                        request.getNewPassword(),
                        user.getPasswordHash()
                )
        ) {

            throw new InvalidRequestException(
                    "New password must be different from old password"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    @Override
    public LoginResponseDTO login(
            LoginRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        log.info("Login service started, ip={}", httpRequest.getRemoteAddr());

        User user =
                userRepository
                        .findByEmail(request.getEmail().trim().toLowerCase())
                        .orElse(null);

        if (user == null) {

            loginAttemptService.saveAttempt(
                    null,
                    request.getEmail(),
                    false,
                    "USER_NOT_FOUND",
                    request.getLatitude(),
                    request.getLongitude(),
                    httpRequest
            );

            log.warn("Login failed because user was not found, ip={}", httpRequest.getRemoteAddr());

            throw new InvalidCredentialsException("Invalid email or password");
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatches) {

            loginAttemptService.saveAttempt(
                    user,
                    request.getEmail(),
                    false,
                    "INVALID_PASSWORD",
                    request.getLatitude(),
                    request.getLongitude(),
                    httpRequest
            );

            log.warn("Login failed because password was invalid, userId={}", user.getId());

            throw new InvalidCredentialsException("Invalid email or password");
        }

        // SUCCESS LOGIN ATTEMPT


        loginAttemptService.saveAttempt(
                user,
                request.getEmail(),
                true,
                "LOGIN_SUCCESS",
                request.getLatitude(),
                request.getLongitude(),
                httpRequest
        );


        if (!passwordMatches) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }
        //user.setLastActivity(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserType()
        );

        loginHistoryService.saveLoginHistory(
                user,
                httpRequest,
                true
        );

        sessionStoreService.createSession(user, token);

        log.info("Login successful, userId={}, role={}", user.getId(), user.getUserType());

        return LoginResponseDTO
                .builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getUserType())
                .token(token)
                .build();
    }

    @Override
    public void sendOtp(SendOtpRequestDTO request) {

        log.info("Send OTP service started");

        User user =
                userRepository
                        .findByEmail(request.getEmail().trim().toLowerCase())
                        .orElseThrow(() -> {
                            log.warn("Send OTP failed because email was not registered");
                            return new InvalidCredentialsException("Email not registered");
                        });

        String otp =
                String.valueOf(
                        100000 + new Random().nextInt(900000)
                );

        EmailOtp emailOtp =
                EmailOtp.builder()
                        .email(user.getEmail())
                        .otp(otp)
                        .expiryTime(LocalDateTime.now().plusMinutes(5))
                        .verified(false)
                        .build();

        emailOtpRepository.save(emailOtp);

        mailService.sendLoginOtp(
                user.getEmail(),
                otp
        );

        log.info("OTP generated and mail triggered, userId={}", user.getId());
    }

    @Override
    public LoginResponseDTO verifyOtp(VerifyOtpRequestDTO request) {

        log.info("Verify OTP service started");

        EmailOtp emailOtp =
                emailOtpRepository
                        .findTopByEmailOrderByIdDesc(request.getEmail())
                        .orElseThrow(() -> {
                            log.warn("OTP verification failed because OTP was not found");
                            return new RuntimeException("OTP not found");
                        });

        if (emailOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            log.warn("OTP verification failed because OTP was expired");
            throw new RuntimeException("OTP expired");
        }

        if (!emailOtp.getOtp().equals(request.getOtp())) {
            log.warn("OTP verification failed because OTP was invalid");
            throw new RuntimeException("Invalid OTP");
        }

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> {
                            log.warn("OTP verification failed because user was not found");
                            return new RuntimeException("User not found");
                        });

     //   user.setLastActivity(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserType()
        );

        sessionStoreService.createSession(user, token);

        emailOtp.setVerified(true);
        emailOtpRepository.save(emailOtp);

        log.info("OTP verified successfully, userId={}, role={}", user.getId(), user.getUserType());

        return LoginResponseDTO
                .builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getUserType())
                .token(token)
                .build();
    }

    @Override
    public void logout(String token) {

        log.info("Logout service started");

        sessionStoreService.deactivateSession(token);

        BlacklistedToken blacklistedToken =
                BlacklistedToken.builder()
                        .token(token)
                        .blacklistedAt(LocalDateTime.now())
                        .build();

        blacklistedTokenRepository.save(blacklistedToken);

        log.info("Logout completed and token blacklisted");
    }

    @Override
    public List<LoginHistoryResponseDTO> getLoginHistory() {

        log.info("Get login history service started");

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> {
                            log.warn("Login history fetch failed because user was not found");
                            return new RuntimeException("User not found");
                        });

        List<LoginHistoryResponseDTO> response =
                loginHistoryService.getLoginHistory(user);

        log.info("Login history fetched successfully, userId={}, count={}", user.getId(), response.size());

        return response;
    }

    @Override
    public List<LoginHistoryResponseDTO> getLoginHistoryByUserId(   Integer userId) {


        log.info("Get login history by userId service started, userId={}", userId);

        List<LoginHistoryResponseDTO> response =
                loginHistoryService.getLoginHistoryByUserId(userId);

        log.info("Login history by userId fetched successfully, userId={}, count={}", userId, response.size());

        return response;
    }
}

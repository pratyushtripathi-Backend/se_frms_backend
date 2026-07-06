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
import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import com.se_frms.user.exception.InvalidCredentialsException;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import com.se_frms.userRole.model.UserRole;
import com.se_frms.userRole.repository.UserRoleRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.se_frms.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.util.DigestUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.lang.Integer;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final String EMPLOYEE_ROLE_NAME = "EMPLOYEE";
    private static final int MAX_OTP_FAILED_ATTEMPTS = 5;
    private static final int MAX_PASSWORD_FAILED_ATTEMPTS = 5;
    private static final int PASSWORD_LOCK_MINUTES = 5;
    private static final SecureRandom OTP_RANDOM = new SecureRandom();
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
    private final RoleMasterRepository roleMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final SmsService smsService;

    @Value("${sms.otp.return-in-response:false}")
    private boolean returnOtpInResponse;

    @Value("${app.frontend.reset-password-url:http://localhost:5173/create-new-password}")
    private String resetPasswordUrl;

    @Override
    public RegistrationResponseDTO registerUser(UserRegistrationRequest request) {

        log.info("Register user service started");

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String phoneNumber = request.getPhoneNumber().trim();

        validateDuplicateEmail(email);
        validateDuplicatePhone(phoneNumber);

        RoleMaster roleMaster = getDefaultEmployeeRoleMaster();
        //Role role = validateAndAssignRole();


        String generatedPassword = PasswordGeneratorUtil.generateSecurePassword();
        String encryptedPassword = passwordEncoder.encode(generatedPassword);

        User user = User.builder()
                .firstName(XssUtil.clean(request.getFirstName().trim()))
                .lastName(XssUtil.clean(request.getLastName().trim()))
                .email(XssUtil.clean(email))
                .phoneNumber(XssUtil.clean(phoneNumber))
                .passwordHash(encryptedPassword)
                .userType(roleMaster.getRoleName())
                .build();

        User savedUser = userRepository.save(user);

        log.info("User saved successfully, userId={}, role={}", savedUser.getId(), savedUser.getUserType());

        saveUserRole(savedUser, roleMaster);

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

    private RoleMaster getDefaultEmployeeRoleMaster() {

        return roleMasterRepository
                .findByRoleNameAndStatus(
                        EMPLOYEE_ROLE_NAME,
                        true
                )
                .orElseThrow(() -> {
                    log.warn("Registration failed because employee role is not configured or inactive");
                    return new InvalidRoleException("Employee role is not configured");
                });
    }

    private void saveUserRole(
            User user,
            RoleMaster roleMaster
    ) {

        UserRole userRole =
                userRoleRepository
                        .findByUserAndRole(user, roleMaster)
                        .orElse(
                                UserRole.builder()
                                        .user(user)
                                        .role(roleMaster)
                                        .build()
                        );

        userRole.setStatus(true);
        userRoleRepository.save(userRole);
    }

    @Override
    public void forgotPassword(
            ForgotPasswordRequest request
    ) {

        log.info(
                "Forgot password service started"
        );

        User user = userRepository

                .findByEmail(
                        request.getEmail()
                )

                .orElseThrow(

                        () -> {

                            log.warn(
                                    "Forgot password failed because user was not found"
                            );

                            return new InvalidRequestException(
                                    "User not found"
                            );

                        }

                );

        byte[] randomBytes =
                new byte[32];

        SecureRandom secureRandom =
                new SecureRandom();

        secureRandom.nextBytes(
                randomBytes
        );

        String rawToken =

                Base64

                        .getUrlEncoder()

                        .withoutPadding()

                        .encodeToString(
                                randomBytes
                        );

        String hashedToken =

                DigestUtils
                        .sha256Hex(
                                rawToken
                        );

        String resetLink =

                resetPasswordUrl
                        + "?token="
                        + rawToken;

        PasswordResetToken resetToken =

                PasswordResetToken
                        .builder()

                        .token(
                                hashedToken
                        )

                        .user(
                                user
                        )

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .used(
                                false
                        )

                        .expiryTime(
                                LocalDateTime.now()
                                        .plusMinutes(15)
                        )

                        .build();

        passwordResetTokenRepository
                .save(
                        resetToken
                );

        log.info(
                "Password reset token created, userId={}",
                user.getId()
        );

        mailService.sendPasswordResetMail(

                user.getEmail(),

                user.getFirstName(),

                resetLink

        );

        log.info(

                "Password reset mail triggered, userId={}",

                user.getId()

        );

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        log.info("Reset password service started");

        if (request.getNewPassword() == null
                || request.getNewPassword().isBlank()) {

            log.warn("Reset password failed because new password was blank");
            throw new InvalidRequestException("New password is required");
        }

        String hashedToken =

                DigestUtils
                        .sha256Hex(
                                request.getToken()
                        );

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(hashedToken)
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

    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    @Override
    public LoginOtpResponseDTO login(
            LoginRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        String email =
                XssUtil.clean(request.getEmail())
                        .trim()
                        .toLowerCase();
        log.info("Sanitized email={}", email);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            loginAttemptService.saveAttempt(
                    null,
                    email,
                    false,
                    "USER_NOT_FOUND",
                    request.getLatitude(),
                    request.getLongitude(),
                    httpRequest
            );

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (!Boolean.TRUE.equals(user.getStatus())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (Boolean.FALSE.equals(user.getStatus())) {

            loginAttemptService.saveAttempt(
                    user,
                    request.getEmail(),
                    false,
                    "USER_BLOCKED",
                    request.getLatitude(),
                    request.getLongitude(),
                    httpRequest
            );

            log.warn("Login failed because user is blacklisted, userId={}", user.getId());

            throw new InvalidCredentialsException("User is blocked. Please contact admin.");
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (user.getPasswordLockedUntil() != null
                && user.getPasswordLockedUntil().isAfter(now)) {

            loginAttemptService.saveAttempt(
                    user,
                    request.getEmail(),
                    false,
                    "PASSWORD_LOCKED",
                    request.getLatitude(),
                    request.getLongitude(),
                    httpRequest
            );

            throw new InvalidCredentialsException(
                    "Account temporarily locked. Please try again after 5 minutes."
            );
        }

        if (user.getPasswordLockedUntil() != null
                && !user.getPasswordLockedUntil().isAfter(now)) {

            user.setFailedPasswordAttempts(0);
            user.setPasswordLockedUntil(null);
            userRepository.save(user);
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!passwordMatches) {

            int failedAttempts =
                    user.getFailedPasswordAttempts() == null
                            ? 0
                            : user.getFailedPasswordAttempts();

            failedAttempts++;

            user.setFailedPasswordAttempts(failedAttempts);

            String reason = "INVALID_PASSWORD";

            if (failedAttempts >= MAX_PASSWORD_FAILED_ATTEMPTS) {

                user.setPasswordLockedUntil(
                        LocalDateTime.now().plusMinutes(PASSWORD_LOCK_MINUTES)
                );

                reason = "PASSWORD_LOCKED";
            }

            userRepository.save(user);

            loginAttemptService.saveAttempt(
                    user,
                    request.getEmail(),
                    false,
                    reason,
                    request.getLatitude(),
                    request.getLongitude(),
                    httpRequest
            );

            log.warn(
                    "Login failed because password was invalid, userId={}, failedAttempts={}",
                    user.getId(),
                    failedAttempts
            );

            if (failedAttempts >= MAX_PASSWORD_FAILED_ATTEMPTS) {
                throw new InvalidCredentialsException(
                        "Too many invalid password attempts. Please try again after 5 minutes."
                );
            }

            throw new InvalidCredentialsException("Invalid email or password");
        }

        user.setFailedPasswordAttempts(0);
        user.setPasswordLockedUntil(null);
        userRepository.save(user);

        String otp =
                String.valueOf(
                        100000 + OTP_RANDOM.nextInt(900000)
                );
        EmailOtp emailOtp =
                EmailOtp.builder()
                        .email(user.getEmail())
                        .otp(otp)
                        .expiryTime(LocalDateTime.now().plusMinutes(5))
                        .verified(false)
                        .failedAttempts(0)
                        .locked(false)
                        .macAddress(cleanMacAddress(request.getMacAddress()))
                        .build();

        emailOtpRepository.save(emailOtp);

        smsService.sendLoginOtp(
                user.getPhoneNumber(),
                otp
        );

        log.info(
                "Login OTP SMS service completed, userId={}",
                user.getId()
        );

        return LoginOtpResponseDTO
                .builder()
                .email(user.getEmail())
                .maskedPhoneNumber(maskPhoneNumber(user.getPhoneNumber()))
                .otp(returnOtpInResponse ? otp : null)
                .otpRequired(true)
                .build();

    }



//    @Override
//    public LoginResponseDTO verifyOtp(
//            VerifyOtpRequestDTO request,
//            HttpServletRequest httpRequest
//    ) {
//
//        String email =
//                request.getEmail().trim().toLowerCase();
//
//        User user =
//                userRepository
//                        .findByEmail(email)
//                        .orElseThrow(
//                                () -> new InvalidCredentialsException(
//                                        "User not found"
//                                )
//                        );
//
//        EmailOtp emailOtp =
//                emailOtpRepository
//                        .findTopByEmailOrderByIdDesc(email)
//                        .orElseThrow(() -> {
//
//                            loginAttemptService.saveAttempt(
//                                    user,
//                                    email,
//                                    false,
//                                    "OTP_NOT_FOUND",
//                                    null,
//                                    null,
//                                    httpRequest
//                            );
//
//                            return new InvalidTokenException(
//                                    "OTP not found"
//                            );
//                        });
//
//        if (Boolean.TRUE.equals(emailOtp.getVerified())) {
//
//            loginAttemptService.saveAttempt(
//                    user,
//                    email,
//                    false,
//                    "OTP_ALREADY_USED",
//                    null,
//                    null,
//                    httpRequest
//            );
//
//            throw new InvalidTokenException(
//                    "OTP already used"
//            );
//        }
//
//        if (emailOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
//
//            emailOtp.setVerified(true);
//            emailOtpRepository.save(emailOtp);
//
//            loginAttemptService.saveAttempt(
//                    user,
//                    email,
//                    false,
//                    "OTP_EXPIRED",
//                    null,
//                    null,
//                    httpRequest
//            );
//
//            throw new TokenExpiredException(
//                    "OTP expired"
//            );
//        }
//
//        if (!emailOtp.getOtp().equals(request.getOtp())) {
//
//            loginAttemptService.saveAttempt(
//                    user,
//                    email,
//                    false,
//                    "INVALID_OTP",
//                    null,
//                    null,
//                    httpRequest
//            );
//
//            throw new InvalidTokenException(
//                    "Invalid OTP"
//            );
//        }
//
//        emailOtp.setVerified(true);
//        emailOtpRepository.save(emailOtp);
//
//        String token = jwtUtil.generateToken(
//                user.getEmail(),
//                user.getUserType()
//        );
//
//        sessionStoreService.createSession(user, token);
//
//        loginAttemptService.saveAttempt(
//                user,
//                email,
//                true,
//                "LOGIN_SUCCESS",
//                null,
//                null,
//                httpRequest
//        );
//
//        loginHistoryService.saveLoginHistory(
//                user,
//                httpRequest,
//                true,
//                resolveMacAddress(
//                        request.getMacAddress(),
//                        emailOtp.getMacAddress()
//                )
//        );
//
//        return LoginResponseDTO
//                .builder()
//                .userId(user.getId())
//                .email(user.getEmail())
//                .role(user.getUserType())
//                .token(token)
//                .build();
//    }

    @Override
    @Transactional(noRollbackFor = {
            InvalidTokenException.class,
            TokenExpiredException.class
    })
    public LoginResponseDTO verifyOtp(
            VerifyOtpRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        String email =
                request.getEmail().trim().toLowerCase();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new InvalidCredentialsException(
                                        "User not found"
                                )
                        );

        EmailOtp emailOtp =
                emailOtpRepository
                        .findTopByEmailOrderByIdDesc(email)
                        .orElseThrow(() -> {

                            loginAttemptService.saveAttempt(
                                    user,
                                    email,
                                    false,
                                    "OTP_NOT_FOUND",
                                    null,
                                    null,
                                    httpRequest
                            );

                            return new InvalidTokenException(
                                    "OTP not found"
                            );
                        });

        if (Boolean.TRUE.equals(emailOtp.getLocked())) {

            loginAttemptService.saveAttempt(
                    user,
                    email,
                    false,
                    "OTP_TOO_MANY_ATTEMPTS",
                    null,
                    null,
                    httpRequest
            );

            throw new InvalidTokenException(
                    "Too many invalid OTP attempts. Please request a new OTP."
            );
        }

        if (Boolean.TRUE.equals(emailOtp.getVerified())) {

            loginAttemptService.saveAttempt(
                    user,
                    email,
                    false,
                    "OTP_ALREADY_USED",
                    null,
                    null,
                    httpRequest
            );

            throw new InvalidTokenException(
                    "OTP already used"
            );
        }

        if (emailOtp.getExpiryTime().isBefore(LocalDateTime.now())) {

            emailOtp.setVerified(true);
            emailOtpRepository.save(emailOtp);

            loginAttemptService.saveAttempt(
                    user,
                    email,
                    false,
                    "OTP_EXPIRED",
                    null,
                    null,
                    httpRequest
            );

            throw new TokenExpiredException(
                    "OTP expired"
            );
        }

        if (!emailOtp.getOtp().equals(request.getOtp())) {

            int failedAttempts =
                    emailOtp.getFailedAttempts() == null
                            ? 1
                            : emailOtp.getFailedAttempts() + 1;

            emailOtp.setFailedAttempts(failedAttempts);

            if (failedAttempts >= MAX_OTP_FAILED_ATTEMPTS) {

                emailOtp.setLocked(true);
                emailOtp.setVerified(true);
                emailOtpRepository.save(emailOtp);

                loginAttemptService.saveAttempt(
                        user,
                        email,
                        false,
                        "OTP_TOO_MANY_ATTEMPTS",
                        null,
                        null,
                        httpRequest
                );

                throw new InvalidTokenException(
                        "Too many invalid OTP attempts. Please request a new OTP."
                );
            }

            emailOtpRepository.save(emailOtp);

            loginAttemptService.saveAttempt(
                    user,
                    email,
                    false,
                    "INVALID_OTP",
                    null,
                    null,
                    httpRequest
            );

            throw new InvalidTokenException(
                    "Invalid OTP"
            );
        }

        emailOtp.setVerified(true);
        emailOtpRepository.save(emailOtp);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserType()
        );

        sessionStoreService.createSession(user, token);

        loginAttemptService.saveAttempt(
                user,
                email,
                true,
                "LOGIN_SUCCESS",
                null,
                null,
                httpRequest
        );

        loginHistoryService.saveLoginHistory(
                user,
                httpRequest,
                true,
                resolveMacAddress(
                        request.getMacAddress(),
                        emailOtp.getMacAddress()
                )
        );

        return LoginResponseDTO
                .builder()
                .userId(user.getId())
                .name(buildFullName(user))
                .email(user.getEmail())
                .role(user.getUserType())
                .token(token)
                .build();
    }

    private String buildFullName(
            User user
    ) {

        String firstName =
                user.getFirstName() == null
                        ? ""
                        : user.getFirstName().trim();

        String lastName =
                user.getLastName() == null
                        ? ""
                        : user.getLastName().trim();

        return (
                firstName
                        + " "
                        + lastName
        ).trim();
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
    public Page<LoginHistoryResponseDTO> getLoginHistory(
            int page,
            int size,
            Map<String, String> filters
    ) {

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

        Page<LoginHistoryResponseDTO> response =
                loginHistoryService.getLoginHistory(
                        user,
                        page,
                        size,
                        filters
                );

        log.info("Login history fetched successfully, userId={}, count={}", user.getId(), response.getNumberOfElements());

        return response;
    }

    @Override
    public Page<LoginHistoryResponseDTO> getLoginHistoryByUserId(
            Integer userId,
            int page,
            int size,
            Map<String, String> filters
    ) {


        log.info("Get login history by userId service started, userId={}", userId);

        Page<LoginHistoryResponseDTO> response =
                loginHistoryService.getLoginHistoryByUserId(
                        userId,
                        page,
                        size,
                        filters
                );

        log.info("Login history by userId fetched successfully, userId={}, count={}", userId, response.getNumberOfElements());

        return response;
    }

    private String maskPhoneNumber(
            String phoneNumber
    ) {

        if (
                phoneNumber == null
                        || phoneNumber.length() < 4
        ) {
            return phoneNumber;
        }

        return "******"
                + phoneNumber.substring(
                phoneNumber.length() - 4
        );
    }

    private String resolveMacAddress(
            String requestMacAddress,
            String storedMacAddress
    ) {

        String cleanedRequestMacAddress =
                cleanMacAddress(requestMacAddress);

        if (cleanedRequestMacAddress != null) {
            return cleanedRequestMacAddress;
        }

        return cleanMacAddress(storedMacAddress);
    }

    private String cleanMacAddress(
            String macAddress
    ) {

        if (macAddress == null || macAddress.isBlank()) {
            return null;
        }

        return XssUtil.clean(macAddress.trim());
    }
}

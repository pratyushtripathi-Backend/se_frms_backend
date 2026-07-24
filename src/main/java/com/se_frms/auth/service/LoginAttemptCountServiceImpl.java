package com.se_frms.auth.service;

import com.se_frms.auth.model.EmailOtp;
import com.se_frms.auth.model.LoginAttemptCount;
import com.se_frms.auth.repository.LoginAttemptCountRepository;
import com.se_frms.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginAttemptCountServiceImpl implements LoginAttemptCountService {

    private static final String PASSWORD = "PASSWORD";
    private static final String OTP = "OTP";
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final LoginAttemptCountRepository repository;

    @Override
    public LoginAttemptCount recordInvalidPasswordAttempt(User user) {

        LoginAttemptCount count =
                repository
                        .findByUserIdAndAttemptTypeAndStatus(
                                user.getId(),
                                PASSWORD,
                                true
                        )
                        .orElseGet(() ->
                                LoginAttemptCount.builder()
                                        .user(user)
                                        .email(user.getEmail())
                                        .attemptType(PASSWORD)
                                        .failedAttempts(0)
                                        .locked(false)
                                        .status(true)
                                        .createdBy(user)
                                        .build()
                        );

        int failedAttempts = count.getFailedAttempts() + 1;

        count.setFailedAttempts(failedAttempts);

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            count.setLocked(true);
            count.setLockedAt(LocalDateTime.now());
            count.setLockReason("5 invalid password attempts");
        }

        return repository.save(count);
    }

    @Override
    public void resetPasswordAttempts(User user) {

        repository
                .findByUserIdAndAttemptTypeAndStatus(
                        user.getId(),
                        PASSWORD,
                        true
                )
                .ifPresent(count -> {
                    count.setFailedAttempts(0);
                    count.setLocked(false);
                    count.setStatus(false);
                    count.setUnlockedAt(LocalDateTime.now());
                    repository.save(count);
                });
    }

    @Override
    public void createOtpAttempt(User user, EmailOtp emailOtp) {

        LoginAttemptCount count =
                LoginAttemptCount.builder()
                        .user(user)
                        .email(user.getEmail())
                        .attemptType(OTP)
                        .emailOtp(emailOtp)
                        .failedAttempts(0)
                        .locked(false)
                        .status(true)
                        .createdBy(user)
                        .build();

        repository.save(count);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOtpLocked(EmailOtp emailOtp) {

        return repository
                .findByEmailOtpIdAndAttemptTypeAndStatus(
                        emailOtp.getId(),
                        OTP,
                        true
                )
                .map(LoginAttemptCount::getLocked)
                .orElse(false);
    }

    @Override
    public LoginAttemptCount recordInvalidOtpAttempt(
            User user,
            EmailOtp emailOtp
    ) {

        LoginAttemptCount count =
                repository
                        .findByEmailOtpIdAndAttemptTypeAndStatus(
                                emailOtp.getId(),
                                OTP,
                                true
                        )
                        .orElseGet(() ->
                                LoginAttemptCount.builder()
                                        .user(user)
                                        .email(user.getEmail())
                                        .attemptType(OTP)
                                        .emailOtp(emailOtp)
                                        .failedAttempts(0)
                                        .locked(false)
                                        .status(true)
                                        .createdBy(user)
                                        .build()
                        );

        int failedAttempts = count.getFailedAttempts() + 1;

        count.setFailedAttempts(failedAttempts);

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            count.setLocked(true);
            count.setLockedAt(LocalDateTime.now());
            count.setLockReason("5 invalid OTP attempts");
        }

        return repository.save(count);
    }

    @Override
    public void resetOtpAttempts(EmailOtp emailOtp) {

        repository
                .findByEmailOtpIdAndAttemptTypeAndStatus(
                        emailOtp.getId(),
                        OTP,
                        true
                )
                .ifPresent(count -> {
                    count.setStatus(false);
                    count.setLocked(false);
                    count.setUnlockedAt(LocalDateTime.now());
                    repository.save(count);
                });
    }

    @Override
    public void markAdminNotificationSent(
            LoginAttemptCount attemptCount
    ) {

        attemptCount.setAdminNotificationSent(true);
        repository.save(attemptCount);
    }
}
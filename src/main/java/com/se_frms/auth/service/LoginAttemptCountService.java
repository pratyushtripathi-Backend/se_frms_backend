package com.se_frms.auth.service;

import com.se_frms.auth.model.EmailOtp;
import com.se_frms.auth.model.LoginAttemptCount;
import com.se_frms.user.model.User;

public interface LoginAttemptCountService {

    LoginAttemptCount recordInvalidPasswordAttempt(User user);

    void resetPasswordAttempts(User user);

    void createOtpAttempt(User user, EmailOtp emailOtp);

    boolean isOtpLocked(EmailOtp emailOtp);

    LoginAttemptCount recordInvalidOtpAttempt(User user, EmailOtp emailOtp);

    void resetOtpAttempts(EmailOtp emailOtp);

    void markAdminNotificationSent(LoginAttemptCount attemptCount);
}
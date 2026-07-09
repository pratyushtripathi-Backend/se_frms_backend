package com.se_frms.mail.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.emailNotification.service.EmailNotificationTemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String LOGIN_CREDENTIALS_TEMPLATE =
            "LOGIN_CREDENTIALS";

    private static final String PASSWORD_RESET_TEMPLATE =
            "PASSWORD_RESET";

    private static final String LOGIN_OTP_TEMPLATE =
            "LOGIN_OTP";
    @Value("${app.admin.security-alert-email:}")
    private String adminSecurityAlertEmail;

    private final JavaMailSender mailSender;

    private final EmailNotificationTemplateService emailNotificationTemplateService;

    @Async
    @Override
    public void sendLoginCredentials(
            String email,
            String firstName,
            String password
    ) {

        log.info("Login credentials mail request received, email={}", email);

        sendMail(
                email,
                resolveSubject(
                        LOGIN_CREDENTIALS_TEMPLATE
                ),
                resolveBody(
                        LOGIN_CREDENTIALS_TEMPLATE,
                        Map.of(
                                "firstName", firstName,
                                "email", email,
                                "password", password
                        )
                )
        );
    }

    @Async
    @Override
    public void sendPasswordResetMail(
            String email,
            String firstName,
            String resetLink
    ) {

        log.info("Password reset mail request received, email={}", email);

        sendMail(
                email,
                resolveSubject(
                        PASSWORD_RESET_TEMPLATE
                ),
                resolveBody(
                        PASSWORD_RESET_TEMPLATE,
                        Map.of(
                                "firstName", firstName,
                                "resetLink", resetLink,
                                "minutes", "15"
                        )
                )
        );
    }

    @Async
    @Override
    public void sendLoginOtp(
            String email,
            String otp
    ) {

        log.info("Login OTP mail request received, email={}", email);

        sendMail(
                email,
                resolveSubject(
                        LOGIN_OTP_TEMPLATE
                ),
                resolveBody(
                        LOGIN_OTP_TEMPLATE,
                        Map.of(
                                "otp", otp,
                                "minutes", "5"
                        )
                )
        );
    }

    private String resolveSubject(
            String templateCode
    ) {

        try {
            return emailNotificationTemplateService.getSubject(templateCode);
        } catch (InvalidRequestException ex) {
            log.warn(
                    "Active email template subject not found, using fallback, templateCode={}",
                    templateCode
            );

            return fallbackSubject(templateCode);
        }
    }

    private String resolveBody(
            String templateCode,
            Map<String, String> values
    ) {

        try {
            return emailNotificationTemplateService.renderTemplate(
                    templateCode,
                    values
            );
        } catch (InvalidRequestException ex) {
            log.warn(
                    "Active email template body not found, using fallback, templateCode={}",
                    templateCode
            );

            return fallbackBody(
                    templateCode,
                    values
            );
        }
    }

    private String fallbackSubject(
            String templateCode
    ) {

        return switch (templateCode) {
            case LOGIN_CREDENTIALS_TEMPLATE -> "FRMS Login Credentials";
            case PASSWORD_RESET_TEMPLATE -> "FRMS Password Reset";
            case LOGIN_OTP_TEMPLATE -> "FRMS Login OTP";
            default -> "FRMS Notification";
        };
    }

    private String fallbackBody(
            String templateCode,
            Map<String, String> values
    ) {

        return switch (templateCode) {
            case LOGIN_CREDENTIALS_TEMPLATE ->
                    "Hello "
                            + value(values, "firstName")
                            + ",\n\nYour FRMS account has been created.\nEmail: "
                            + value(values, "email")
                            + "\nPassword: "
                            + value(values, "password")
                            + "\n\nPlease login and change your password.";
            case PASSWORD_RESET_TEMPLATE ->
                    "Hello "
                            + value(values, "firstName")
                            + ",\n\nUse the link below to reset your FRMS password. This link is valid for "
                            + value(values, "minutes")
                            + " minutes.\n"
                            + value(values, "resetLink");
            case LOGIN_OTP_TEMPLATE ->
                    "Your FRMS login OTP is "
                            + value(values, "otp")
                            + ". It is valid for "
                            + value(values, "minutes")
                            + " minutes.";
            default -> "FRMS notification";
        };
    }

    private String value(
            Map<String, String> values,
            String key
    ) {

        return values.getOrDefault(
                key,
                ""
        );
    }

    private void sendMail(
            String to,
            String subject,
            String body
    ) {

        try {
            log.info("Sending mail, to={}, subject={}", to, subject);

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Mail sent successfully, to={}", to);

        } catch (Exception ex) {

            log.error("Failed to send mail, to={}", to, ex);

            throw ex;
        }
    }

    @Async
    @Override
    public void sendPasswordAttemptLockAlert(
            String userEmail,
            String userName,
            Integer userId,
            Integer failedAttempts
    ) {

        if (adminSecurityAlertEmail == null
                || adminSecurityAlertEmail.isBlank()) {

            log.warn("Admin security alert email is not configured");
            return;
        }

        sendMail(
                adminSecurityAlertEmail,
                "FRMS Account Locked - Failed Login Attempts",
                "Hello Admin,\n\n"
                        + "User account has been locked due to multiple wrong password attempts.\n\n"
                        + "User Id: " + userId + "\n"
                        + "Name: " + userName + "\n"
                        + "Email: " + userEmail + "\n"
                        + "Failed Attempts: " + failedAttempts + "\n\n"
                        + "Please review and unblock from the admin portal if required."
        );
    }
}

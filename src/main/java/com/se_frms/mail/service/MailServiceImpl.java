package com.se_frms.mail.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.emailNotification.service.EmailNotificationTemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
                emailNotificationTemplateService.getSubject(
                        LOGIN_CREDENTIALS_TEMPLATE
                ),
                emailNotificationTemplateService.renderTemplate(
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
                emailNotificationTemplateService.getSubject(
                        PASSWORD_RESET_TEMPLATE
                ),
                emailNotificationTemplateService.renderTemplate(
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
                emailNotificationTemplateService.getSubject(
                        LOGIN_OTP_TEMPLATE
                ),
                emailNotificationTemplateService.renderTemplate(
                        LOGIN_OTP_TEMPLATE,
                        Map.of(
                                "otp", otp,
                                "minutes", "5"
                        )
                )
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
}
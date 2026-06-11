package com.se_frms.mail.service;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.emailNotification.service.EmailNotificationTemplateService;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailServiceImpl
        implements MailService {

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

        Map<String, String> values =
                Map.of(
                        "firstName", firstName,
                        "email", email,
                        "password", password
                );

        sendMail(
                email,
                getSubjectOrDefault(
                        LOGIN_CREDENTIALS_TEMPLATE,
                        "Your FRMS login credentials"
                ),
                renderTemplateOrDefault(
                        LOGIN_CREDENTIALS_TEMPLATE,
                        values,
                        "Hello {{firstName}},\n\n"
                                + "Your FRMS account has been created.\n"
                                + "Email: {{email}}\n"
                                + "Password: {{password}}\n\n"
                                + "Please log in and change your password."
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

        Map<String, String> values =
                Map.of(
                        "firstName", firstName,
                        "resetLink", resetLink,
                        "minutes", "15"
                );

        sendMail(
                email,
                getSubjectOrDefault(
                        PASSWORD_RESET_TEMPLATE,
                        "Reset your FRMS password"
                ),
                renderTemplateOrDefault(
                        PASSWORD_RESET_TEMPLATE,
                        values,
                        "Hello {{firstName}},\n\n"
                                + "Use this link to reset your password:\n"
                                + "{{resetLink}}\n\n"
                                + "This link expires in {{minutes}} minutes."
                )
        );
    }

    @Async
    @Override
    public void sendLoginOtp(
            String email,
            String otp
    ) {

        Map<String, String> values =
                Map.of(
                        "otp", otp,
                        "minutes", "5"
                );

        sendMail(
                email,
                getSubjectOrDefault(
                        LOGIN_OTP_TEMPLATE,
                        "Your FRMS login OTP"
                ),
                renderTemplateOrDefault(
                        LOGIN_OTP_TEMPLATE,
                        values,
                        "Your login OTP is {{otp}}.\n\n"
                                + "This OTP expires in {{minutes}} minutes."
                )
        );
    }

    private String getSubjectOrDefault(
            String templateCode,
            String defaultSubject
    ) {

        try {
            String subject =
                    emailNotificationTemplateService.getSubject(
                            templateCode
                    );

            if (subject == null || subject.isBlank()) {
                return defaultSubject;
            }

            return subject;
        } catch (InvalidRequestException ex) {
            return defaultSubject;
        }
    }

    private String renderTemplateOrDefault(
            String templateCode,
            Map<String, String> values,
            String defaultBody
    ) {

        try {
            return emailNotificationTemplateService.renderTemplate(
                    templateCode,
                    values
            );
        } catch (InvalidRequestException ex) {
            return replacePlaceholders(
                    defaultBody,
                    values
            );
        }
    }

    private String replacePlaceholders(
            String body,
            Map<String, String> values
    ) {

        for (Map.Entry<String, String> entry : values.entrySet()) {
            body = body.replace(
                    "{{" + entry.getKey() + "}}",
                    entry.getValue()
            );
        }

        return body;
    }

    private void sendMail(
            String to,
            String subject,
            String body
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}

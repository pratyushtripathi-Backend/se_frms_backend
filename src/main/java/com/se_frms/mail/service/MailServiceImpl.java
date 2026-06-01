package com.se_frms.mail.service;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl
        implements MailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendLoginCredentials(
            String email,
            String firstName,
            String password
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "FRMS Login Credentials"
        );

        message.setText(
                "Hello " + firstName + ",\n\n" +
                        "Your FRMS account has been created successfully.\n\n" +
                        "Email: " + email + "\n" +
                        "Password: " + password + "\n\n" +
                        "Please change your password after your first login.\n\n" +
                        "Regards,\n" +
                        "FRMS Team"
        );

        mailSender.send(message);
    }

    @Async
    @Override
    public void sendPasswordResetMail(
            String email,
            String firstName,
            String resetLink
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "FRMS Password Reset Request"
        );

        message.setText(
                "Hello " + firstName + ",\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Please use the link below to set a new password:\n\n" +
                        resetLink + "\n\n" +
                        "This link will expire in 15 minutes.\n\n" +
                        "If you did not request this password reset, " +
                        "please ignore this email.\n\n" +
                        "Regards,\n" +
                        "FRMS Team"
        );

        mailSender.send(message);
    }
}
package com.se_frms.mail.service;

public interface MailService {

    void sendLoginCredentials(
            String email,
            String firstName,
            String password
    );

    void sendPasswordResetMail(
            String email,
            String firstName,
            String resetLink
    );
}



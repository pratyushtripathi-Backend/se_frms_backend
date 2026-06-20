package com.se_frms.auth.repository;

import com.se_frms.auth.model.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository
        extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailOrderByIdDesc(
            String email
    );

}
package com.se_frms.auth.repository;

import com.se_frms.auth.model.LoginAttemptCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginAttemptCountRepository
        extends JpaRepository<LoginAttemptCount, Integer> {

    Optional<LoginAttemptCount> findByUserIdAndAttemptTypeAndStatus(
            Integer userId,
            String attemptType,
            Boolean status
    );

    Optional<LoginAttemptCount> findByEmailOtpIdAndAttemptTypeAndStatus(
            Long emailOtpId,
            String attemptType,
            Boolean status
    );
}
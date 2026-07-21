package com.se_frms.auth.repository;

import com.se_frms.auth.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoginAttemptRepository
        extends JpaRepository<LoginAttempt, Integer>,
        JpaSpecificationExecutor<LoginAttempt> {
}
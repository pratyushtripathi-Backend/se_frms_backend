package com.se_frms.auth.repository;



import com.se_frms.auth.model.LoginAttempt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoginAttemptRepository
        extends JpaRepository
        <
                LoginAttempt,
                UUID
                > {



    List<LoginAttempt> findByUserIdOrderByAttemptedAtDesc(UUID userId);


}
package com.se_frms.auth.repository;

import com.se_frms.auth.model.SessionStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionStoreRepository
        extends JpaRepository<SessionStore, Integer> {

    Optional<SessionStore> findByTokenAndStatus(
            String token,
            Boolean status
    );

    boolean existsByTokenAndStatus(
            String token,
            Boolean status
    );

    Optional<SessionStore> findByToken(
            String token
    );
}
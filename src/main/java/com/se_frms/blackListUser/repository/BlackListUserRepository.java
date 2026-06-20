package com.se_frms.blackListUser.repository;

import com.se_frms.blackListUser.model.BlackListUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BlackListUserRepository
        extends JpaRepository<BlackListUser, Integer>,
        JpaSpecificationExecutor<BlackListUser> {

    Optional<BlackListUser> findTopByUserIdAndStatusOrderByCreatedDateDesc(
            Integer userId,
            Boolean status
    );

    boolean existsByUserIdAndStatus(
            Integer userId,
            Boolean status
    );

    Page<BlackListUser> findByStatus(
            Boolean status,
            Pageable pageable
    );

    Page<BlackListUser> findByUserIdAndStatus(
            Integer userId,
            Boolean status,
            Pageable pageable
    );
}

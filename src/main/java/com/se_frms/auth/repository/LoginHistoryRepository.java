package com.se_frms.auth.repository;


import com.se_frms.auth.model.LoginHistory;

import com.se_frms.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.lang.Integer;

@Repository
public interface LoginHistoryRepository
        extends JpaRepository<
        LoginHistory,
        Long
        > {

    List<LoginHistory> findByUserOrderByCreatedDateDesc(
            User user
    );


    List<LoginHistory>
    findByUserIdOrderByCreatedDateDesc(
            Integer userId
    );
}

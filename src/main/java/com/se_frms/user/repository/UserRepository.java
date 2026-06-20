package com.se_frms.user.repository;



import com.se_frms.user.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository
        extends JpaRepository<User, Integer>,
        JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);


    Optional<User> findById(Integer id);

    List<User> findByUserTypeOrderByFirstNameAscLastNameAsc(
            String userType
    );

    List<User> findByUserTypeAndStatusOrderByFirstNameAscLastNameAsc(
            String userType,
            Boolean status
    );

    Page<User> findByUserTypeAndStatusOrderByFirstNameAscLastNameAsc(
            String userType,
            Boolean status,
            Pageable pageable
    );
}



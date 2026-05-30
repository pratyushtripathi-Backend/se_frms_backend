package com.se_frms.user.repository;



import com.se_frms.user.dto.UserResponseDTO;
import com.se_frms.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository
        extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);


    UserResponseDTO getUserById(UUID id);

    Optional<User> findById(UUID id);
}



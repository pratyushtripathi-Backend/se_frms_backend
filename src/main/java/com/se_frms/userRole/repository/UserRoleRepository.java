package com.se_frms.userRole.repository;

import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.user.model.User;
import com.se_frms.userRole.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository
        extends JpaRepository<UserRole, Integer> {

    Optional<UserRole> findByUserAndRole(
            User user,
            RoleMaster role
    );

    List<UserRole> findByUser(User user);

    List<UserRole> findByUserAndStatus(
            User user,
            Boolean status
    );

    List<UserRole> findByStatus(Boolean status);
}
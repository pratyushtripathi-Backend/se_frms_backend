package com.se_frms.permission.repository;



import com.se_frms.permission.enums.Permission;
import com.se_frms.permission.model.UserPermission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPermissionRepository
        extends JpaRepository<
        UserPermission,
        UUID
        > {

    List<UserPermission>
    findByUserId(
            UUID userId
    );

    Optional<UserPermission>
    findByUserIdAndPermission(
            UUID userId,
            Permission permission
    );

    boolean existsByUserIdAndPermission(
            UUID userId,
            Permission permission
    );

    void deleteByUserId(
            UUID userId
    );

    void deleteByUserIdAndPermission(
            UUID userId,
            Permission permission
    );
}

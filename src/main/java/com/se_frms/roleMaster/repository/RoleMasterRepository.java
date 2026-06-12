package com.se_frms.roleMaster.repository;

import com.se_frms.roleMaster.model.RoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleMasterRepository
        extends JpaRepository<RoleMaster, Integer> {

    boolean existsByRoleName(String roleName);

    Optional<RoleMaster> findByRoleName(String roleName);

    Optional<RoleMaster> findByRoleNameAndStatus(
            String roleName,
            Boolean status
    );

    List<RoleMaster> findByStatus(Boolean status);
}

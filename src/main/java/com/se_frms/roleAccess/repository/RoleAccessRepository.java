package com.se_frms.roleAccess.repository;

import com.se_frms.roleAccess.model.RoleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleAccessRepository
        extends JpaRepository<RoleAccess, Integer>,
        JpaSpecificationExecutor<RoleAccess> {
    List<RoleAccess>

    findByRoleRoleIdAndStatusTrue(

            Integer roleId

    );

    List<RoleAccess>
    findByRoleRoleIdAndStatusTrueOrderByAccessAccessNameAsc(

            Integer roleId

    );

    List<RoleAccess>
    findByStatusTrueOrderByRoleRoleNameAscAccessAccessNameAsc();

    boolean existsByRoleRoleIdAndAccessId(
            Integer roleId,
            Integer accessId
    );

    boolean existsByStatusTrueAndAccessAccessNameIgnoreCaseAndAccessStatusTrueAndRoleRoleIdIn(
            String accessName,
            List<Integer> roleIds
    );

    Optional<RoleAccess>

    findByRoleRoleIdAndAccessId(

            Integer roleId,

            Integer accessId

    );
    List<RoleAccess> findByRoleRoleId(
            Integer roleId
    );

}

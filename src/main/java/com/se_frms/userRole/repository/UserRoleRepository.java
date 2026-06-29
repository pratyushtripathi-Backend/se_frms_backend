package com.se_frms.userRole.repository;

import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.user.model.User;
import com.se_frms.userRole.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository
        extends JpaRepository<UserRole, Integer>,
        JpaSpecificationExecutor<UserRole> {

    Optional<UserRole> findByUserAndRole(
            User user,
            RoleMaster role
    );

    List<UserRole> findByUser(User user);

    List<UserRole> findByUserOrderByRoleRoleNameAsc(
            User user
    );

    List<UserRole> findByUserAndStatus(
            User user,
            Boolean status
    );

    Page<UserRole> findAllByOrderByUserFirstNameAscUserLastNameAsc(
            Pageable pageable
    );

    List<UserRole> findByStatusOrderByUserFirstNameAscUserLastNameAsc(
            Boolean status
    );

    @Query("""
            select ur.role.roleId
            from UserRole ur
            where ur.user.id = :userId
              and ur.status = true
              and ur.role.status = true
            """)
    List<Integer> findActiveRoleIdsByUserId(
            @Param("userId") Integer userId
    );
}

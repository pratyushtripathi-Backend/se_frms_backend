package com.se_frms.common.security;

import com.se_frms.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RoleHierarchyServiceTest {

    private RoleHierarchyService roleHierarchyService;

    @BeforeEach
    void setUp() {
        roleHierarchyService = new RoleHierarchyService();
    }

    @Test
    void superAdminShouldInheritAdminPermissions() {
        Set<String> authorities = roleHierarchyService.buildAuthorities(Role.SUPER_ADMIN).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorities).contains(
                Permission.SYSTEM_ADMIN.value(),
                Permission.AUDIT_LOGS_READ.value(),
                Permission.USERS_READ.value(),
                "ROLE_SUPER_ADMIN",
                "ROLE_ADMIN",
                "ROLE_USER"
        );
    }

    @Test
    void userShouldOnlyReceiveSelfServicePermissions() {
        Set<String> authorities = roleHierarchyService.buildAuthorities(Role.USER).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertThat(authorities).contains(
                Permission.PROFILE_READ_SELF.value(),
                Permission.PROFILE_UPDATE_SELF.value(),
                "ROLE_USER"
        );
        assertThat(authorities).doesNotContain(Permission.USERS_READ.value());
    }
}

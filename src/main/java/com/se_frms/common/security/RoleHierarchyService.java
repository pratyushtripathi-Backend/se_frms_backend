package com.se_frms.common.security;

import com.se_frms.user.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleHierarchyService {

    private static final Map<Role, Set<Role>> IMPLIED_ROLES = new EnumMap<>(Role.class);
    private static final Map<Role, Set<Permission>> DIRECT_PERMISSIONS = new EnumMap<>(Role.class);

    static {
        IMPLIED_ROLES.put(Role.EMPLOYEE, EnumSet.of(Role.EMPLOYEE));
        IMPLIED_ROLES.put(Role.USER, EnumSet.of(Role.USER));
        IMPLIED_ROLES.put(Role.MANAGER, EnumSet.of(Role.MANAGER, Role.USER));
        IMPLIED_ROLES.put(Role.DEPARTMENT_HEAD, EnumSet.of(Role.DEPARTMENT_HEAD, Role.MANAGER, Role.USER));
        IMPLIED_ROLES.put(Role.HR, EnumSet.of(Role.HR, Role.USER));
        IMPLIED_ROLES.put(Role.ADMIN, EnumSet.of(Role.ADMIN, Role.HR, Role.DEPARTMENT_HEAD, Role.MANAGER, Role.USER));
        IMPLIED_ROLES.put(Role.SUPER_ADMIN, EnumSet.of(Role.SUPER_ADMIN, Role.ADMIN, Role.HR, Role.DEPARTMENT_HEAD, Role.MANAGER, Role.USER));

        DIRECT_PERMISSIONS.put(Role.EMPLOYEE, EnumSet.of(
                Permission.AUTH_LOGIN,
                Permission.AUTH_REGISTER,
                Permission.PROFILE_READ_SELF,
                Permission.PROFILE_UPDATE_SELF
        ));
        DIRECT_PERMISSIONS.put(Role.USER, EnumSet.of(
                Permission.AUTH_LOGIN,
                Permission.AUTH_REGISTER,
                Permission.PROFILE_READ_SELF,
                Permission.PROFILE_UPDATE_SELF
        ));
        DIRECT_PERMISSIONS.put(Role.MANAGER, EnumSet.of(
                Permission.USERS_READ
        ));
        DIRECT_PERMISSIONS.put(Role.DEPARTMENT_HEAD, EnumSet.of(
                Permission.DEPARTMENT_USERS_READ,
                Permission.USERS_UPDATE
        ));
        DIRECT_PERMISSIONS.put(Role.HR, EnumSet.of(
                Permission.USERS_READ,
                Permission.USERS_UPDATE,
                Permission.USERS_BLOCK,
                Permission.USERS_UNBLOCK
        ));
        DIRECT_PERMISSIONS.put(Role.ADMIN, EnumSet.of(
                Permission.AUDIT_LOGS_READ,
                Permission.RULES_MANAGE,
                Permission.ALERTS_MANAGE,
                Permission.USERS_ASSIGN_ROLE
        ));
        DIRECT_PERMISSIONS.put(Role.SUPER_ADMIN, EnumSet.of(
                Permission.SECURITY_CONFIG_MANAGE,
                Permission.SYSTEM_ADMIN
        ));
    }

    public Set<Role> expandRoles(Role role) {
        if (role == null) {
            return EnumSet.noneOf(Role.class);
        }

        return EnumSet.copyOf(IMPLIED_ROLES.getOrDefault(role, EnumSet.of(role)));
    }

    public Set<Permission> expandPermissions(Role role) {
        Set<Role> roles = expandRoles(normalize(role));
        Set<Permission> permissions = EnumSet.noneOf(Permission.class);

        for (Role expandedRole : roles) {
            permissions.addAll(DIRECT_PERMISSIONS.getOrDefault(expandedRole, EnumSet.noneOf(Permission.class)));
        }

        return permissions;
    }

    public Collection<? extends GrantedAuthority> buildAuthorities(Role role) {
        Set<GrantedAuthority> authorities = expandPermissions(role).stream()
                .map(permission -> new SimpleGrantedAuthority(permission.value()))
                .collect(Collectors.toSet());

        expandRoles(normalize(role)).stream()
                .map(expandedRole -> new SimpleGrantedAuthority("ROLE_" + expandedRole.name()))
                .forEach(authorities::add);

        return authorities;
    }

    public Role normalize(Role role) {
        if (role == Role.EMPLOYEE) {
            return Role.USER;
        }

        return role;
    }
}

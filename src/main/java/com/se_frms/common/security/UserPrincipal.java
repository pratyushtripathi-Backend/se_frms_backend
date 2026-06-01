package com.se_frms.common.security;

import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserPrincipal implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(
            UUID userId,
            String email,
            String passwordHash,
            Role role,
            boolean active,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserPrincipal fromUser(
            User user,
            RoleHierarchyService roleHierarchyService
    ) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                roleHierarchyService.normalize(user.getRole()),
                Boolean.TRUE.equals(user.getIsActive()),
                roleHierarchyService.buildAuthorities(user.getRole())
        );
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}

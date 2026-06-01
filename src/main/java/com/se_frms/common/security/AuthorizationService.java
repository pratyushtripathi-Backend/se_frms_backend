package com.se_frms.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authorizationService")
public class AuthorizationService {

    public boolean hasPermission(
            Authentication authentication,
            String permission
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(permission));
    }

    public boolean canAccessUser(
            Authentication authentication,
            UUID targetUserId,
            String permission
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal userPrincipal)) {
            return false;
        }

        if (userPrincipal.getUserId().equals(targetUserId)) {
            return hasPermission(authentication, Permission.PROFILE_READ_SELF.value())
                    || hasPermission(authentication, permission);
        }

        return hasPermission(authentication, permission);
    }
}

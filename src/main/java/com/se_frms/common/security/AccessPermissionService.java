package com.se_frms.common.security;

import com.se_frms.roleAccess.repository.RoleAccessRepository;
import com.se_frms.user.model.User;
import com.se_frms.userRole.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessPermissionService {

    private final CurrentUserService currentUserService;
    private final UserRoleRepository userRoleRepository;
    private final RoleAccessRepository roleAccessRepository;

    public void validateAccess(String accessName) {

        User currentUser =
                currentUserService.getCurrentUser();

        List<Integer> activeRoleIds =
                userRoleRepository
                        .findActiveRoleIdsByUserId(
                                currentUser.getId()
                        );

        if (activeRoleIds.isEmpty()) {
            throw new AccessDeniedException("Access denied");
        }

        boolean hasAccess =
                roleAccessRepository
                        .existsByStatusTrueAndAccessAccessNameIgnoreCaseAndAccessStatusTrueAndRoleRoleIdIn(
                                accessName,
                                activeRoleIds
                        );

        if (!hasAccess) {
            throw new AccessDeniedException("Access denied");
        }
    }
}

package com.se_frms.admin.loader;

import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import com.se_frms.userRole.model.UserRole;
import com.se_frms.userRole.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminDataLoader
        implements CommandLineRunner {

    private static final String ADMIN_ROLE_NAME = "ADMIN";

    private static final List<String> DEFAULT_ROLE_NAMES =
            List.of(
                    ADMIN_ROLE_NAME,
                    "EMPLOYEE",
                    "USER"
            );

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleMasterRepository roleMasterRepository;

    private final UserRoleRepository userRoleRepository;

    @Override
    public void run(
            String... args
    ) {

        seedRoles();

        User admin = userRepository
                .findByEmail("admin@frms.com")
                .orElseGet(this::createAdminUser);

        RoleMaster adminRole = roleMasterRepository
                .findByRoleNameAndStatus(ADMIN_ROLE_NAME, true)
                .orElseThrow();

        UserRole userRole = userRoleRepository
                .findByUserAndRole(admin, adminRole)
                .orElse(
                        UserRole.builder()
                                .user(admin)
                                .role(adminRole)
                                .build()
                );

        userRole.setStatus(true);
        userRoleRepository.save(userRole);
    }

    private void seedRoles() {
        DEFAULT_ROLE_NAMES
                .forEach(roleName -> {
                    RoleMaster roleMaster = roleMasterRepository
                            .findByRoleName(roleName)
                            .orElse(
                                    RoleMaster.builder()
                                            .roleName(roleName)
                                            .build()
                            );

                    roleMaster.setStatus(true);
                    roleMasterRepository.save(roleMaster);
                });
    }

    private User createAdminUser() {
        User admin =
                User.builder()
                        .firstName("System")
                        .lastName("Admin")
                        .email("admin@frms.com")
                        .phoneNumber("9839307509")
                        .passwordHash(
                                passwordEncoder.encode(
                                        "Admin@123"
                                )
                        )
                        .userType(ADMIN_ROLE_NAME)
                        .status(true)
                        .build();

        return userRepository.save(admin);
    }
}

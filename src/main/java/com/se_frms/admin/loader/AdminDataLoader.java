package com.se_frms.admin.loader;

import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import com.se_frms.userRole.model.UserRole;
import com.se_frms.userRole.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AdminDataLoader
        implements CommandLineRunner {

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
                .findByRoleNameAndStatus(Role.ADMIN.name(), true)
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
        Arrays.stream(Role.values())
                .forEach(role -> {
                    RoleMaster roleMaster = roleMasterRepository
                            .findByRoleName(role.name())
                            .orElse(
                                    RoleMaster.builder()
                                            .roleName(role.name())
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
                        .phoneNumber("9999999999")
                        .passwordHash(
                                passwordEncoder.encode(
                                        "Admin@123"
                                )
                        )
                        .userType(Role.ADMIN.name())
                        .status(true)
                        .build();

        return userRepository.save(admin);
    }
}

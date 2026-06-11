package com.se_frms.admin.loader;

import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataLoader
        implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(
            String... args
    ) {

        if (
                userRepository.existsByEmail(
                        "admin@frms.com"
                )
        ) {
            return;
        }

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
                        .userType("ADMIN")
                        .build();

        userRepository.save(admin);
    }
}

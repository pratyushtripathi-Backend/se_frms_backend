package com.se_frms.common.security;

import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            throw new InvalidRequestException("Logged-in user not found");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new InvalidRequestException(
                                "Logged-in user not found"
                        )
                );
    }

    public Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
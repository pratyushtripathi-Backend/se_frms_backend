package com.se_frms.common.service;

import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedByResolver {

    private final UserRepository userRepository;

    public String resolve(Integer userId) {

        if (userId == null) {
            return null;
        }

        return userRepository
                .findById(userId)
                .map(this::resolve)
                .orElse(null);
    }

    public String resolve(User user) {

        if (user == null) {
            return null;
        }

        String firstName =
                user.getFirstName() == null
                        ? ""
                        : user.getFirstName().trim();

        String lastName =
                user.getLastName() == null
                        ? ""
                        : user.getLastName().trim();

        String fullName =
                (firstName + " " + lastName).trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        return null;
    }
}

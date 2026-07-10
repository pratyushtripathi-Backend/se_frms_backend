package com.se_frms.user.service;



import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.common.security.XssUtil;
import com.se_frms.user.dto.UpdateUserRequest;

import com.se_frms.user.dto.UserResponseDTO;

import com.se_frms.user.model.User;

import com.se_frms.user.repository.UserRepository;

import com.se_frms.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.lang.Integer;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;

    private final CurrentUserService currentUserService;

    @Override
    public UserResponseDTO getUserById(
            Integer id
    ) {

        if (id == null) {

            throw new InvalidRequestException(
                    "User id cannot be null"
            );
        }

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new InvalidRequestException(
                                        "User not found with id: " + id
                                )
                        );

        return UserResponseDTO.builder()

                .id(user.getId())

                .firstName(user.getFirstName())

                .lastName(user.getLastName())

                .email(user.getEmail())

                .phoneNumber(user.getPhoneNumber())

                .role(user.getUserType())

                .build();
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(
            Integer id,
            UpdateUserRequest request
    ) {

        if (id == null) {
            throw new InvalidRequestException(
                    "User id cannot be null"
            );
        }

        User currentUser =
                currentUserService.getCurrentUser();

        boolean admin =
                "ADMIN".equalsIgnoreCase(
                        currentUser.getUserType()
                );

        if (!admin && !currentUser.getId().equals(id)) {
            throw new InvalidRequestException(
                    "You can update only your own profile"
            );
        }

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new InvalidRequestException(
                                        "User not found with id: " + id
                                )
                        );

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        String phoneNumber =
                request.getPhoneNumber()
                        .trim();

        if (!Objects.equals(user.getEmail(), email)
                && userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                    "Email already exists"
            );
        }

        if (!Objects.equals(user.getPhoneNumber(), phoneNumber)
                && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicatePhoneException(
                    "Phone number already exists"
            );
        }

        user.setFirstName(
                XssUtil.clean(
                        request.getFirstName().trim()
                )
        );
        user.setLastName(
                XssUtil.clean(
                        request.getLastName().trim()
                )
        );
        user.setEmail(
                XssUtil.clean(email)
        );
        user.setPhoneNumber(
                XssUtil.clean(phoneNumber)
        );

        User savedUser =
                userRepository.save(user);

        return mapToResponse(
                savedUser
        );
    }

    private UserResponseDTO mapToResponse(
            User user
    ) {

        return UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getUserType())
                .build();
    }
}

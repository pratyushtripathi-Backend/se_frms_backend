package com.se_frms.user.service;



import com.se_frms.auth.exception.InvalidRequestException;

import com.se_frms.user.dto.UserResponseDTO;

import com.se_frms.user.model.User;

import com.se_frms.user.repository.UserRepository;

import com.se_frms.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO getUserById(
            UUID id
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

                .role(user.getRole())

                .build();
    }
}

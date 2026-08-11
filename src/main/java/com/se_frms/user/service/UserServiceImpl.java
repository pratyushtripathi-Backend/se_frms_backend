package com.se_frms.user.service;



import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.common.security.XssUtil;
import com.se_frms.common.service.CreatedByResolver;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.user.dto.UpdateUserRequest;

import com.se_frms.user.dto.UserResponseDTO;

import com.se_frms.user.model.User;
import java.time.LocalDateTime;
import com.se_frms.user.repository.UserRepository;

import com.se_frms.user.service.UserService;
import com.se_frms.user.dto.UserStatusRequestDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.lang.Integer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl
        implements UserService {

    private static final Map<String, String> USER_FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("firstName", "firstName"),
                    Map.entry("lastName", "lastName"),
                    Map.entry("email", "email"),
                    Map.entry("phoneNumber", "phoneNumber"),
                    Map.entry("role", "userType"),
                    Map.entry("userType", "userType"),
                    Map.entry("status", "status"),
                    Map.entry("createdBy", "createdBy.id"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final UserRepository userRepository;

    private final CurrentUserService currentUserService;
    private final CreatedByResolver createdByResolver;

    @Override
    public Page<UserResponseDTO> getAllUsers(
            int page,
            int size,
            Map<String, String> filters
    ) {

        Map<String, String> userFilters =
                new HashMap<>(
                        filters == null
                                ? Map.of()
                                : filters
                );

        String search =
                userFilters.remove(
                        "search"
                );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        userFilters,
                        USER_FILTER_FIELDS,
                        "createdDate",
                        Sort.Direction.DESC
                );

        Specification<User> specification =
                DynamicFilterSpecification
                        .<User>build(
                                userFilters,
                                USER_FILTER_FIELDS
                        )
                        .and(
                                buildUserSearchSpecification(
                                        search
                                )
                        );

        return userRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public Page<UserResponseDTO> getAllNonAdminUsers(
            int page,
            int size,
            Map<String, String> filters
    ) {

        Map<String, String> userFilters =
                new HashMap<>(
                        filters == null
                                ? Map.of()
                                : filters
                );

        String search =
                userFilters.remove(
                        "search"
                );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        userFilters,
                        USER_FILTER_FIELDS,
                        "createdDate",
                        Sort.Direction.DESC
                );

        Specification<User> specification =
                DynamicFilterSpecification
                        .<User>build(
                                userFilters,
                                USER_FILTER_FIELDS
                        )
                        .and(
                                (root, query, criteriaBuilder) ->
                                        criteriaBuilder.notEqual(
                                                criteriaBuilder.upper(
                                                        root.get("userType")
                                                ),
                                                "ADMIN"
                                        )
                        )
                        .and(
                                buildUserSearchSpecification(
                                        search
                                )
                        );

        return userRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);
    }

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

        return mapToResponse(user);
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

        boolean emailChanged =
                !Objects.equals(
                        user.getEmail(),
                        email
                );

        boolean logoutRequired =
                currentUser.getId().equals(id)
                        && emailChanged;

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
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser =
                userRepository.save(user);

        UserResponseDTO response =
                mapToResponse(
                        savedUser
                );

        response.setLogoutRequired(
                logoutRequired
        );

        return response;
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserStatus(
            Integer id,
            UserStatusRequestDTO request
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

        user.setStatus(request.getStatus());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser =
                userRepository.save(user);

        return mapToResponse(savedUser);
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
                .status(user.getStatus())
                .createdBy(
                        createdByResolver.resolve(user.getCreatedBy())
                )
                .createdDate(user.getCreatedDate())
                .updatedAt(user.getUpdatedAt())
                .logoutRequired(false)
                .build();
    }

    private Specification<User> buildUserSearchSpecification(
            String search
    ) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern =
                    "%"
                            + search
                            .trim()
                            .toLowerCase(Locale.ROOT)
                            + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("firstName")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("lastName")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("email")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("phoneNumber")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("userType")),
                            pattern
                    )
            );
        };
    }
}

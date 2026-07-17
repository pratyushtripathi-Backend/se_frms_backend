package com.se_frms.admin.service;
import com.se_frms.common.security.CurrentUserService;
import com.se_frms.admin.dto.CreateEmployeeRequest;
import com.se_frms.admin.dto.EmployeeResponseDTO;
import com.se_frms.admin.dto.EmployeeSummaryDTO;
import com.se_frms.admin.dto.UpdateEmployeePatchRequest;
import com.se_frms.admin.dto.UpdateEmployeeRequest;
import com.se_frms.auth.dto.RegistrationResponseDTO;
import com.se_frms.auth.exception.DuplicateEmailException;
import com.se_frms.auth.exception.DuplicatePhoneException;
import com.se_frms.auth.exception.InvalidRequestException;
import com.se_frms.auth.util.PasswordGeneratorUtil;
import com.se_frms.common.security.XssUtil;
import com.se_frms.common.util.DynamicFilterSpecification;
import com.se_frms.mail.service.MailService;
import com.se_frms.roleMaster.model.RoleMaster;
import com.se_frms.roleMaster.repository.RoleMasterRepository;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import com.se_frms.userRole.model.UserRole;
import com.se_frms.userRole.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final String EMPLOYEE_ROLE_NAME = "EMPLOYEE";

    private static final Map<String, String> EMPLOYEE_FILTER_FIELDS =
            Map.ofEntries(
                    Map.entry("id", "id"),
                    Map.entry("firstName", "firstName"),
                    Map.entry("lastName", "lastName"),
                    Map.entry("email", "email"),
                    Map.entry("phoneNumber", "phoneNumber"),
                    Map.entry("userType", "userType"),
                    Map.entry("status", "status"),
                    Map.entry("createdById", "createdBy.id"),
                    Map.entry("createdDate", "createdDate"),
                    Map.entry("updatedAt", "updatedAt")
            );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RoleMasterRepository roleMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final CurrentUserService currentUserService;

    @Override
    public RegistrationResponseDTO createEmployee(CreateEmployeeRequest request) {
        log.info("Create employee service started");

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);
        String phoneNumber = request.getPhoneNumber().trim();

        if (userRepository.existsByEmail(email)) {
            log.warn("Create employee failed because email already exists");
            throw new DuplicateEmailException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            log.warn("Create employee failed because phone number already exists");
            throw new DuplicatePhoneException("Phone already registered");
        }

        RoleMaster roleMaster = getRoleMasterForCreateUser(request.getRoleName());

        String generatedPassword = PasswordGeneratorUtil.generateSecurePassword();
        String encryptedPassword = passwordEncoder.encode(generatedPassword);
        User loggedInAdmin =
                currentUserService.getCurrentUser();
        User employee = User.builder()
                .firstName(XssUtil.clean(request.getFirstName().trim()))
                .lastName(XssUtil.clean(request.getLastName().trim()))
                .email(XssUtil.clean(email))
                .phoneNumber(XssUtil.clean(phoneNumber))
                .passwordHash(encryptedPassword)
                .userType(roleMaster.getRoleName())
                .status(true)
                .createdBy(loggedInAdmin)
                .build();

        User savedEmployee = userRepository.save(employee);

        log.info(
                "Employee saved successfully, employeeId={}, role={}",
                savedEmployee.getId(),
                savedEmployee.getUserType()
        );

        saveUserRole(savedEmployee, roleMaster);
        log.info("Employee role mapping saved, employeeId={}", savedEmployee.getId());

        mailService.sendLoginCredentials(
                savedEmployee.getEmail(),
                savedEmployee.getFirstName(),
                generatedPassword
        );
        log.info("Employee login credentials mail triggered, employeeId={}", savedEmployee.getId());

        return RegistrationResponseDTO.builder()
                .userId(savedEmployee.getId())
                .firstName(savedEmployee.getFirstName())
                .lastName(savedEmployee.getLastName())
                .email(savedEmployee.getEmail())
                .phoneNumber(savedEmployee.getPhoneNumber())
                .role(savedEmployee.getUserType())
                .status(savedEmployee.getStatus())
                .createdDate(savedEmployee.getCreatedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Integer employeeId) {
        log.info("Get employee by id service started, employeeId={}", employeeId);

        User employee = getEmployeeOrThrow(employeeId);

        if (!isEmployee(employee)) {
            log.warn("User is not an employee, userId={}, role={}", employeeId, employee.getUserType());
            throw new InvalidRequestException("User is not employee");
        }

        validateActiveEmployee(employee, employeeId);

        log.info("Employee fetched successfully, employeeId={}", employeeId);
        return map(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSummaryDTO> getAllEmployees() {
        log.info("Get all employees service started");

        List<EmployeeSummaryDTO> employees = userRepository
                .findByUserTypeAndStatusOrderByFirstNameAscLastNameAsc(
                        EMPLOYEE_ROLE_NAME,
                        true
                )
                .stream()
                .map(user -> EmployeeSummaryDTO.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getUserType())
                        .status(user.getStatus())
                        .createdBy(
                                buildCreatedBy(user.getCreatedBy())
                        )
                        .createdDate(user.getCreatedDate())
                        .updatedAt(user.getUpdatedAt())
                        .build()
                )
                .toList();

        log.info("Employees fetched successfully, count={}", employees.size());
        return employees;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeSummaryDTO> getAllEmployees(
            int page,
            int size
    ) {
        return getAllEmployees(
                page,
                size,
                Map.of()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeSummaryDTO> getAllEmployees(
            int page,
            int size,
            Map<String, String> filters
    ) {
        log.info(
                "Get all employees service started, page={}, size={}",
                page,
                size
        );

        Map<String, String> employeeFilters =
                new HashMap<>(
                        filters
                );

        String search =
                employeeFilters.remove(
                        "search"
                );

        Pageable pageable =
                DynamicFilterSpecification.createPageable(
                        page,
                        size,
                        employeeFilters,
                        EMPLOYEE_FILTER_FIELDS,
                        "firstName",
                        Sort.Direction.ASC
                );

        Specification<User> specification =
                DynamicFilterSpecification
                        .<User>equal(
                                "userType",
                                EMPLOYEE_ROLE_NAME
                        )
                        .and(
                                DynamicFilterSpecification.equal(
                                        "status",
                                        true
                                )
                        )
                        .and(
                                DynamicFilterSpecification.build(
                                        employeeFilters,
                                        EMPLOYEE_FILTER_FIELDS
                                )
                        )
                        .and(
                                buildEmployeeSearchSpecification(
                                        search
                                )
                        );

        Page<EmployeeSummaryDTO> employees =
                userRepository
                        .findAll(
                                specification,
                                pageable
                        )
                        .map(user -> EmployeeSummaryDTO.builder()
                                .id(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .phoneNumber(user.getPhoneNumber())
                                .role(user.getUserType())
                                .status(user.getStatus())
                                .createdBy(
                                        buildCreatedBy(user.getCreatedBy())
                                )
                                .createdDate(user.getCreatedDate())
                                .updatedAt(user.getUpdatedAt())
                                .build()
                        );

        log.info(
                "Employees fetched successfully, page={}, size={}, count={}",
                employees.getNumber(),
                employees.getSize(),
                employees.getNumberOfElements()
        );

        return employees;
    }

    private String buildCreatedBy(User createdBy) {

        if (createdBy == null) {
            return null;
        }

        String createdByName =
                (
                        (createdBy.getFirstName() != null ? createdBy.getFirstName() : "")
                                + " "
                                + (createdBy.getLastName() != null ? createdBy.getLastName() : "")
                ).trim();

        if (createdByName.isBlank()) {
            return null;
        }

        return createdByName;
    }

    private Specification<User> buildEmployeeSearchSpecification(
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
                    )
            );
        };
    }

    @Override
    public void deleteEmployee(Integer employeeId) {
        log.info("Delete employee service started, employeeId={}", employeeId);

        User employee = getEmployeeOrThrow(employeeId);

        if (!isEmployee(employee)) {
            log.warn("Delete employee failed because user is not employee, userId={}, role={}", employeeId, employee.getUserType());
            throw new InvalidRequestException("Only employee can be deleted");
        }

        if (Boolean.FALSE.equals(employee.getStatus())) {
            log.warn("Delete employee failed because employee is already inactive, employeeId={}", employeeId);
            throw new InvalidRequestException("Employee is already inactive");
        }

        employee.setStatus(false);
        userRepository.save(employee);

        log.info("Employee deleted successfully, employeeId={}", employeeId);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Integer employeeId, UpdateEmployeeRequest request) {
        log.info("Update employee service started, employeeId={}", employeeId);

        User user = getEmployeeOrThrow(employeeId);

        if (!isEmployee(user)) {
            log.warn("Update employee failed because user is not employee, userId={}, role={}", employeeId, user.getUserType());
            throw new InvalidRequestException("Only employee can be updated");
        }

        validateActiveEmployee(user, employeeId);

        validateUniqueEmailForUpdate(user, request.getEmail(), employeeId);
        validateUniquePhoneForUpdate(user, request.getPhoneNumber(), employeeId);

        user.setFirstName(XssUtil.clean(request.getFirstName()));
        user.setLastName(XssUtil.clean(request.getLastName()));
        user.setEmail(XssUtil.clean(request.getEmail()));
        user.setPhoneNumber(XssUtil.clean(request.getPhoneNumber()));
        user.setStatus(request.getIsActive());

        User savedUser = userRepository.save(user);

        log.info("Employee updated successfully, employeeId={}", employeeId);
        return map(savedUser);
    }

    @Override
    public EmployeeResponseDTO patchEmployee(Integer employeeId, UpdateEmployeePatchRequest request) {
        log.info("Patch employee service started, employeeId={}", employeeId);

        User user = getEmployeeOrThrow(employeeId);

        if (!isEmployee(user)) {
            log.warn("Patch employee failed because user is not employee, userId={}, role={}", employeeId, user.getUserType());
            throw new InvalidRequestException("Only employee can be updated");
        }

        validateActiveEmployee(user, employeeId);

        validateUniqueEmailForPatch(user, request.getEmail(), employeeId);
        validateUniquePhoneForPatch(user, request.getPhoneNumber(), employeeId);

        if (request.getFirstName() != null) {
            user.setFirstName(XssUtil.clean(request.getFirstName()));
        }

        if (request.getLastName() != null) {
            user.setLastName(XssUtil.clean(request.getLastName()));
        }

        if (request.getEmail() != null) {
            user.setEmail(XssUtil.clean(request.getEmail()));
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(XssUtil.clean(request.getPhoneNumber()));
        }

        if (request.getIsActive() != null) {
            user.setStatus(request.getIsActive());
        }

        User savedUser = userRepository.save(user);

        log.info("Employee patched successfully, employeeId={}", employeeId);
        return map(savedUser);
    }

    private User getEmployeeOrThrow(Integer employeeId) {
        return userRepository
                .findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found, employeeId={}", employeeId);
                    return new InvalidRequestException("Employee not found");
                });
    }

    private boolean isEmployee(User user) {
        return EMPLOYEE_ROLE_NAME.equals(user.getUserType());
    }

    private void validateActiveEmployee(User employee, Integer employeeId) {
        if (Boolean.FALSE.equals(employee.getStatus())) {
            log.warn("Employee is inactive, employeeId={}", employeeId);
            throw new InvalidRequestException("Employee is inactive");
        }
    }

    private void validateUniqueEmailForUpdate(User user, String email, Integer employeeId) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);

        if (normalizedEmail != null
                && !user.getEmail().equals(normalizedEmail)
                && userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Update employee failed because email already exists, employeeId={}", employeeId);
            throw new DuplicateEmailException("Email already exists");
        }
    }

    private void validateUniquePhoneForUpdate(User user, String phoneNumber, Integer employeeId) {
        String normalizedPhoneNumber = phoneNumber == null ? null : phoneNumber.trim();

        if (normalizedPhoneNumber != null
                && !user.getPhoneNumber().equals(normalizedPhoneNumber)
                && userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
            log.warn("Update employee failed because phone already exists, employeeId={}", employeeId);
            throw new DuplicatePhoneException("Phone already exists");
        }
    }

    private void validateUniqueEmailForPatch(User user, String email, Integer employeeId) {
        if (email == null) {
            return;
        }

        validateUniqueEmailForUpdate(user, email, employeeId);
    }

    private void validateUniquePhoneForPatch(User user, String phoneNumber, Integer employeeId) {
        if (phoneNumber == null) {
            return;
        }

        validateUniquePhoneForUpdate(user, phoneNumber, employeeId);
    }

    private EmployeeResponseDTO map(User user) {
        return EmployeeResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getUserType())
                .status(user.getStatus())
                .createdBy(
                        buildCreatedBy(user.getCreatedBy())
                )
                .createdDate(user.getCreatedDate())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private RoleMaster getRoleMasterForCreateUser(String roleName) {
        String finalRoleName = roleName == null || roleName.isBlank()
                ? EMPLOYEE_ROLE_NAME
                : roleName.trim().toUpperCase(Locale.ROOT);

        return roleMasterRepository
                .findByRoleNameAndStatus(finalRoleName, true)
                .orElseThrow(() -> {
                    log.warn("Create employee failed because role is invalid or inactive, roleName={}", finalRoleName);
                    return new InvalidRequestException("Invalid or inactive role");
                });
    }

    private void saveUserRole(User user, RoleMaster roleMaster) {
        UserRole userRole = userRoleRepository
                .findByUserAndRole(user, roleMaster)
                .orElse(UserRole.builder()
                        .user(user)
                        .role(roleMaster)
                        .build()
                );

        userRole.setStatus(true);
        userRoleRepository.save(userRole);
    }
}

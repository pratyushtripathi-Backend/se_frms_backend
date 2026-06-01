package com.se_frms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se_frms.auth.dto.UserRegistrationRequest;
import com.se_frms.common.security.JwtUtil;
import com.se_frms.user.enums.Role;
import com.se_frms.user.model.User;
import com.se_frms.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User normalUser;
    private User secondUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        normalUser = userRepository.save(User.builder()
                .firstName("Asha")
                .lastName("User")
                .email("asha@example.com")
                .phoneNumber("9876543210")
                .passwordHash(passwordEncoder.encode("Password@123"))
                .role(Role.USER)
                .isActive(true)
                .build());

        secondUser = userRepository.save(User.builder()
                .firstName("Rohit")
                .lastName("User")
                .email("rohit@example.com")
                .phoneNumber("9876543211")
                .passwordHash(passwordEncoder.encode("Password@123"))
                .role(Role.USER)
                .isActive(true)
                .build());

        adminUser = userRepository.save(User.builder()
                .firstName("Anika")
                .lastName("Admin")
                .email("admin@example.com")
                .phoneNumber("9876543212")
                .passwordHash(passwordEncoder.encode("Password@123"))
                .role(Role.ADMIN)
                .isActive(true)
                .build());
    }

    @Test
    void shouldRejectUnauthenticatedProfileAccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", normalUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowUsersToReadTheirOwnProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", normalUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(normalUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.responseData.email").value("asha@example.com"));
    }

    @Test
    void shouldBlockUsersFromReadingOtherProfilesWithoutPermission() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", secondUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(normalUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToReadAnotherUsersProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", normalUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(adminUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.id").value(normalUser.getId().toString()));
    }

    @Test
    void shouldRejectUnsafeRegistrationPayloads() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("<script>alert(1)</script>");
        request.setLastName("Tester");
        request.setEmail("test@example.com");
        request.setPhoneNumber("9876543219");
        request.setPassword("Password@123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    void shouldExposeCsrfTokenEndpointAndCookie() throws Exception {
        mockMvc.perform(get("/api/v1/security/csrf-token"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(jsonPath("$.headerName").value("X-XSRF-TOKEN"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtUtil.generateToken(user);
    }
}

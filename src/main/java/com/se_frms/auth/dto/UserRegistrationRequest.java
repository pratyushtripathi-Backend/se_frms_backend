package com.se_frms.auth.dto;

import com.se_frms.common.security.validation.SafeText;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {

    @NotBlank(
            message = "First name is required"
    )
    @Size(
            min = 2,
            max = 50,
            message = "First name must be between 2 and 50 characters"
    )
    @Pattern(
            regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "Invalid first name"
    )
    @SafeText
    private String firstName;

    @NotBlank(
            message = "Last name is required"
    )
    @Size(
            min = 2,
            max = 50,
            message = "Last name must be between 2 and 50 characters"
    )
    @Pattern(
            regexp = "^[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "Invalid last name"
    )
    @SafeText
    private String lastName;

    @NotBlank(
            message = "Email is required"
    )
    @Email(
            message = "Invalid email format"
    )
    @Size(
            max = 150,
            message = "Email must not exceed 150 characters"
    )
    @SafeText
    private String email;

    @NotBlank(
            message = "Phone number is required"
    )
    @Pattern(
            regexp = "^(\\+91)?[6-9]\\d{9}$",
            message = "Invalid Indian phone number"
    )
    @SafeText
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            max = 20,
            message = "Password must be between 8 and 20 characters"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!&*]).{8,20}$",
            message = "Password must include uppercase, lowercase, digit, and special character"
    )
    private String password;
}

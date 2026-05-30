package com.se_frms.auth.dto;

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
    private String email;

    @NotBlank(
            message = "Phone number is required"
    )
    @Pattern(
            regexp = "^(\\+91)?[6-9]\\d{9}$",
            message = "Invalid Indian phone number"
    )
    private String phoneNumber;
}


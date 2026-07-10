package com.se_frms.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoginRequestDTO {

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;


    @JsonAlias({"lat", "loginLatitude"})
    private BigDecimal latitude;

    @JsonAlias({"lng", "loginLongitude"})
    private BigDecimal longitude;

    private ClientLocationDTO location;

    private String macAddress;
}

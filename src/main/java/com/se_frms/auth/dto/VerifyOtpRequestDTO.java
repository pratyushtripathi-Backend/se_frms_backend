package com.se_frms.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VerifyOtpRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String otp;

    private String macAddress;

    @JsonAlias({"lat", "loginLatitude"})
    private BigDecimal latitude;

    @JsonAlias({"lng", "loginLongitude"})
    private BigDecimal longitude;

    private ClientLocationDTO location;
}

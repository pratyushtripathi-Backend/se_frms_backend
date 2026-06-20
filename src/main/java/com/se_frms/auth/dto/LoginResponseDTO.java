package com.se_frms.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.lang.Integer;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO extends LoginOtpResponseDTO {

    private Integer userId;

    private String email;

    private String role;

    private String token;
}
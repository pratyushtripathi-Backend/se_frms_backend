package com.se_frms.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.lang.Integer;

@Getter
@Builder
public class RegistrationResponseDTO {

    private Integer userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String role;

    private Boolean status;

    private LocalDateTime createdDate;
}

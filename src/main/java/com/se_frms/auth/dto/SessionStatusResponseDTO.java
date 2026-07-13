package com.se_frms.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.lang.Integer;

@Data
@Builder
public class SessionStatusResponseDTO {

    private Boolean active;

    private String name;

    private String email;

    private String role;

    private LocalDate sessionActiveDate;

    private LocalTime sessionActiveTime;
}
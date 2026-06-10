package com.se_frms.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class SessionStatusResponseDTO {

    private Boolean active;

    private UUID userId;

    private String email;

    private String role;

    private LocalDate sessionActiveDate;

    private LocalTime sessionActiveTime;
}
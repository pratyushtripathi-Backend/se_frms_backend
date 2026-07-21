package com.se_frms.auth.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptResponseDTO {

    private Integer id;

    private String name;

    private String email;

    private Boolean status;

    private String reason;

    private String ipAddress;

    private String createdBy;

    private LocalDate attemptDate;

    private LocalTime attemptTime;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}
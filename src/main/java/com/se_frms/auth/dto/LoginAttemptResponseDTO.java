package com.se_frms.auth.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptResponseDTO {

    private UUID id;

    private UUID userId;

    private String email;

    private Boolean attemptStatus;

    private String attemptReason;

    private String ipAddress;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String url;

    private LocalDateTime attemptedAt;

    private LocalDateTime createdDate;
}
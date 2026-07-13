package com.se_frms.auth.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.lang.Integer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptResponseDTO {

    private Integer id;

    private String name;

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
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
public class LoginHistoryResponseDTO {

    private Long id;

    private String name;

    private LocalDate loginDate;

    private LocalTime loginTime;

    private String ipAddress;

    private String macAddress;

    private Double latitude;

    private Double longitude;

    private String url;

    private Boolean status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;

}

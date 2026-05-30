package com.se_frms.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RegistrationResponseDTO {
    private UUID userId;
}

package com.se_frms.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}
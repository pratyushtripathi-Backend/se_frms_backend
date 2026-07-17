package com.se_frms.emailNotification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmailNotificationTemplateStatusRequestDTO {

    @NotNull(message = "Status is required")
    private Boolean status;
}
